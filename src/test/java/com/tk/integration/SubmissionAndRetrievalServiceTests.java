package com.tk.integration;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
//import org.springframework.test.web.reactive.server.WebTestClient.MockSpec;

import com.tk.integration.service.SubmissionAndRetrievalService;

@ExtendWith(MockitoExtension.class)
@WebFluxTest
@AutoConfigureWebTestClient
public class SubmissionAndRetrievalServiceTests {

	@Autowired
	private WebTestClient webTestClient;

	@MockBean
	private SubmissionAndRetrievalService submissionAndRetrievalService;

//	@Test
//	public void testSubmitAndRetrieve() {
//		ClassPathResource fileResource = new ClassPathResource("test-cv.pdf");
//
//		MockSpec MockSpec = webTestClient.post()
//				.uri("/api/submit")
//				.header("account", "testAccount")
//				.header("username", "testUser")
//				.header("password", "testPassword")
//				.contentType(MediaType.MULTIPART_FORM_DATA)
//				.bodyValue(fileResource);
//
//		Mockito.when(submissionAndRetrievalService.submit(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
//				.thenReturn(Mono.just("123456"));
//
//		webTestClient.post()
//				.uri("/api/submit")
//				.header("account", "testAccount")
//				.header("username", "testUser")
//				.header("password", "testPassword")
//				.contentType(MediaType.MULTIPART_FORM_DATA)
//				.bodyValue(fileResource)
//				.exchange()
//				.expectStatus().isOk()
//				.expectBody(String.class)
//				.returnResult().getResponseBody();
//
//		webTestClient.get()
//				.uri("/api/retrieve/" + "123456")
//				.exchange()
//				.expectStatus().isOk();
//	}
//}
}