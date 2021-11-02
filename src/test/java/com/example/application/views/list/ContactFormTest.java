package com.example.application.views.list;

import com.example.application.data.entity.Company;
import com.example.application.data.entity.Contact;
import com.example.application.data.entity.Status;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class ContactFormTest {

    private Contact marcUsher;
    private Company company1;
    private Company company2;
    private Status status1;
    private Status status2;
    private ContactForm contactForm;

    @Before
    public void setupData() {
        List<Company> companies = new ArrayList<>();
        company1 = new Company();
        company1.setName("Vaadin Ltd");
        company2 = new Company();
        company2.setName("IT Mill");
        companies.add(company1);
        companies.add(company2);

        List<Status> statuses = new ArrayList<>();
        status1 = new Status();
        status1.setName("Status 1");
        status2 = new Status();
        status2.setName("Status 2");
        statuses.add(status1);
        statuses.add(status2);

        marcUsher = new Contact();
        marcUsher.setFirstName("Marc");
        marcUsher.setLastName("Usher");
        marcUsher.setEmail("marc@usher.com");
        marcUsher.setStatus(status1);
        marcUsher.setCompany(company2);

        contactForm = new ContactForm(companies, statuses);
    }

    @Test
    public void initialize_shouldPopulateCompanyFormField() {
        List<Company> companies = contactForm.company.getListDataView()
                .getItems()
                .collect(Collectors.toList());
        assertEquals(2, companies.size());
        assertTrue(companies.contains(company1));
        assertTrue(companies.contains(company2));
    }

    @Test
    public void initialize_shouldShowStatusNameAsItemValue() {
        String name = contactForm.status
                .getItemLabelGenerator()
                .apply(status1);
        assertEquals("Status 1", name);
    }

    @Test
    public void initialize_shouldPopulateStatusFormField() {
        List<Status> statuses = contactForm.status.getListDataView()
                .getItems()
                .collect(Collectors.toList());
        assertEquals(2, statuses.size());
        assertTrue(statuses.contains(status1));
        assertTrue(statuses.contains(status2));
        assertEquals("Status",contactForm.status.getLabel());
    }

    @Test
    public void initialize_shouldShowCompanyNameAsItemValue() {
        String name = contactForm.company
                .getItemLabelGenerator()
                .apply(company1);
        assertEquals("Vaadin Ltd", name);
    }

    @Test
    public void setContact_whenContactIsNotNull_shouldBindItsPropertiesIntoFormFields() {
        contactForm.setContact(marcUsher);
        assertEquals("Marc", contactForm.firstName.getValue());
        assertEquals("Usher", contactForm.lastName.getValue());
        assertEquals("marc@usher.com", contactForm.email.getValue());
        assertEquals(company2, contactForm.company.getValue());
        assertEquals(status1, contactForm.status.getValue());
    }

    @Test
    public void setContact_whenContactIsNull_shouldBindEmptyIntoFormFields() {
        contactForm.setContact(null);
        assertEquals("", contactForm.firstName.getValue());
        assertEquals("", contactForm.lastName.getValue());
        assertEquals("", contactForm.email.getValue());
        assertNull(contactForm.company.getValue());
        assertNull(contactForm.status.getValue());
    }

    @Test
    public void fireSaveEvent_() {
        final String firstName = "John";
        final String lastName = "Doe";
        final String email = "john@doe.com";

        contactForm.firstName.setValue(firstName);
        contactForm.lastName.setValue(lastName);
        contactForm.email.setValue(email);
        contactForm.company.setValue(company1);
        contactForm.status.setValue(status1);

        AtomicReference<Contact> savedContactReference = new AtomicReference<>();
        contactForm.addListener(ContactForm.SaveEvent.class, e -> {
            savedContactReference.set(e.getContact());
        });
        contactForm.save.click();
        Contact savedContact = savedContactReference.get();
        assertEquals(firstName, savedContact.getFirstName());
        assertEquals(lastName, savedContact.getLastName());
        assertEquals(email, savedContact.getEmail());
        assertEquals(company1, savedContact.getCompany());
        assertEquals(status1, savedContact.getStatus());
    }
}
