package com.example.customer_service.service;

import com.example.customer_service.config.RestTemplateConfig;
import com.example.customer_service.model.Customer;
import com.example.customer_service.model.CustomerDTO;
import com.example.customer_service.model.CustomerResult;
import com.example.customer_service.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public class CustomerServiceTest {
    private final Long id = -1L;
    @Mock
    private RestTemplateConfig restTemplateConfig;
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private CustomerRepository customerRepo;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private CustomerService customerService;
    private Customer customer;
    @BeforeEach
    public void setup() {
        when(restTemplateConfig.restTemplate()).thenReturn(restTemplate);
        customer = new Customer(
                "Test Customer",
                "Test111@mail.com",
                "Test Street 1",
                "123456",
                "TestPassWord"
        );
        customer.setId(id);
    }

    @Test
    public void getCustomerByIdReturnsCustomer() {
        when(customerRepo.findById(id)).thenReturn(Optional.of(customer));
        CustomerDTO result = customerService.getCustomerById(id);

        assertNotNull(result);
        assertEquals("Test Customer", result.getName());

        verify(customerRepo).findById(id);
    }

    @Test
    void updateCustomer_ShouldUpdateCustomer_WithNewPassword() {
        Customer updatedData = new Customer("TestTest Customer", "TestTest@mail.com", "TestTest Street 1", "123456", "TestPassWord");
        updatedData.setId(id);
        CustomerDTO dto = new CustomerDTO(updatedData.getId(), updatedData.getName(), updatedData.getEmail(), updatedData.getAddress(), updatedData.getPhone());
        dto.setPassword("TestPassWord");
        when(customerRepo.findById(id)).thenReturn(Optional.of(customer));
        when(passwordEncoder.encode("TestPassWord")).thenReturn("TestTestPassWord");
        when(customerRepo.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));
        CustomerResult result = customerService.updateCustomer(id, dto);
        assertEquals("TestTest Customer", result.dto().getName());
        assertEquals("TestTest@mail.com", result.dto().getEmail());
        verify(customerRepo).save(customer);
        assertEquals("TestTestPassWord", customer.getPassword());
    }
}