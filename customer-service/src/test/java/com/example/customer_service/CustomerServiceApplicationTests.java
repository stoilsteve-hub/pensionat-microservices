package com.example.customer_service;

import com.example.customer_service.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
@AutoConfigureMockMvc
class CustomerServiceApplicationTests {
	@Autowired
	MockMvc mvc;

	@Autowired
	private CustomerRepository customerRepo;
	@BeforeEach
	void cleanDatabase() {
		customerRepo.deleteAll();
	}

	@Test
	void contextLoads() {
	}

	@Test
	void shouldCreateCustomer() throws Exception {
		String customerJson = """
    {
        "name": "Test Customer",
        "email": "test@test.com",
        "address": "Test Street 1",
        "phone": "0701234567",
        "password": "password"
    }
    """;
		mvc.perform(MockMvcRequestBuilders.post("/api/customers/signup")
						.contentType(MediaType.APPLICATION_JSON).content(customerJson))
				.andExpect(MockMvcResultMatchers.status().isCreated())
				.andExpect(MockMvcResultMatchers.jsonPath("$.customer.id").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.customer.email").value("test@test.com"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.token").exists());
	}

	@Test
	void shouldReturnBadRequestOnMissingEmail() throws Exception {
		String invalidCustomerJson = """
        {
            "name": "Test Customer",
            "email": "",
            "password": "password"
        }
        """;
		mvc.perform(MockMvcRequestBuilders.post("/api/customers/signup").contentType(MediaType.APPLICATION_JSON)
				.content(invalidCustomerJson)).andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	void shouldReturnNotFoundForInvalidCustomer() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/api/customers/999999"))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}
}