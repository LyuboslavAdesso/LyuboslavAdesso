package com.example.application.views.list;

import com.example.application.data.entity.Contact;
import com.example.application.data.service.CrmService;
import com.example.application.views.MainView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;
import java.util.Objects;

//01. Creating contact view
@Route(value = "", layout = MainView.class)
//@Theme moved to the parent layout or to the main launch class
@PageTitle("Contacts | Vaadin CRM")
public class ContactListView extends VerticalLayout {

    final Grid<Contact> contactTable = new Grid<>(Contact.class);

    final TextField contactFilter = new TextField();

    final ContactForm contactForm;

    private final CrmService service;

    public ContactListView(CrmService service) {
        this.service = service;
        addClassNames("list-view");
        setSizeFull();
        contactForm = new ContactForm(service.findAllCompanies(), service.findAllStatuses());
        configureGrid();
        configureForm();
        add(getToolbar(), getContent()); // places all child components vertically to the vertical layout

        updateList();
        closeEditor();
    }

    private void configureGrid() {
        contactTable.addClassNames("contact-grid");
        contactTable.setSizeFull();
        contactTable.setColumns("firstName", "lastName", "email");
        contactTable.addColumn(contact -> contact.getStatus().getName()).setHeader("Status"); //Define custom columns for nested objects.
        contactTable.addColumn(contact -> contact.getCompany().getName()).setHeader("Company");
        contactTable.getColumns()
                .forEach(column -> column.setAutoWidth(true));

        contactTable.asSingleSelect()
                .addValueChangeListener(event -> editContact(event.getValue()));
    }

    private void configureForm() {
        contactForm.setWidth("25em");
        contactForm.addListener(ContactForm.SaveEvent.class, this::saveContact);
        contactForm.addListener(ContactForm.DeleteEvent.class, this::deleteContact);
        contactForm.addListener(ContactForm.CloseEvent.class, e -> closeEditor());
    }

    private HorizontalLayout getToolbar() {
        contactFilter.setPlaceholder("Filter by name...");
        contactFilter.setClearButtonVisible(true);

        //Configure the search field to fire value-change events only when the user stops typing.
        //ValueChangeMode -> makes a call from the client to the server
        contactFilter.setValueChangeMode(ValueChangeMode.LAZY);
        contactFilter.addValueChangeListener(event -> updateList()); //Update the table after filtering

        Button addContactButton = new Button("Add contact");
        addContactButton.addClickListener(clickEvent -> addContact());

        HorizontalLayout toolbar = new HorizontalLayout(contactFilter, addContactButton);
        toolbar.addClassNames("toolbar");
        return toolbar;
    }

    private Component getContent() {
        HorizontalLayout content = new HorizontalLayout(contactTable, contactForm);
        content.setFlexGrow(2, contactTable);
        content.setFlexGrow(1, contactForm);
        content.addClassName("content");
        content.setSizeFull();
        return content;
    }

    //Vaadin is not reactive -> sync content on every UI update
    private void updateList() {
        List<Contact> contacts = service.findAllContacts(contactFilter.getValue());
        contactTable.setItems(contacts);
    }

    private void addContact() {
        contactTable.asSingleSelect().clear();
        editContact(new Contact());
    }

    private void closeEditor() {
        contactForm.setContact(null);
        contactForm.setVisible(false);
        removeClassName("editing");
    }

    private void editContact(Contact contact) {
        if (Objects.isNull(contact)) {
            closeEditor();
        } else {
            contactForm.setContact(contact);
            contactForm.setVisible(true);
            addClassName("editing");
        }
    }

    private void saveContact(ContactForm.SaveEvent event) {
        executeServiceRequest(() -> service.saveContact(event.getContact()));
    }

    private void deleteContact(ContactForm.DeleteEvent event) {
        executeServiceRequest(() -> service.deleteContact(event.getContact()));
    }

    private void executeServiceRequest(Runnable runnable) {
        runnable.run();
        updateList();
        closeEditor();
    }
}
