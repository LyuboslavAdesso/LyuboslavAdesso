package com.example.application.views.list;

import com.example.application.data.entity.Company;
import com.example.application.data.entity.Contact;
import com.example.application.data.entity.Status;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;

import java.util.List;
import java.util.Locale;

@JsModule("./date-format.js")
@NpmPackage(value = "date-fns", version = "2.16.0")
public class ContactForm extends FormLayout {

    //Form elements
    final TextField firstName = new TextField("First name");
    final TextField lastName = new TextField("Last name");
    final EmailField email = new EmailField("Email");
    final DatePicker datePicker = new DatePicker();
    final ComboBox<Company> company = new ComboBox<>("Company");
    final ComboBox<Status> status = new ComboBox<>("Status");
    final Button save = new Button("Save");
    final Button delete = new Button("Delete");
    final Button close = new Button("Close");

    private final Binder<Contact> binder = new BeanValidationBinder<>(Contact.class);

    private Contact contact = new Contact();

    public ContactForm(List<Company> companies, List<Status> statuses) {
        addClassName("contact-form");
        binder.bindInstanceFields(this); // -> matches fields in Contact and ContactForm based on their names.

        company.setItems(companies);
        company.setItemLabelGenerator(Company::getName);
        status.setItems(statuses);
        status.setItemLabelGenerator(Status::getName);

        HorizontalLayout buttonsLayout = createButtonsLayout();

        //TESTING -> date format of the date picker
        datePicker.setLocale(Locale.CHINESE);
        datePicker.addOpenedChangeListener((l ->
                UI.getCurrent()
                .beforeClientResponse(
                        datePicker,
                        ctx -> datePicker.getElement().executeJs("window._setDatePickerFormats(this);"))
        ));
        datePicker.addValueChangeListener(event -> {
            System.out.println(event.getValue());
        });

        add(firstName, lastName, email, datePicker, company, status, buttonsLayout);
    }

    private HorizontalLayout createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickShortcut(Key.ENTER);
        save.addClickListener(event -> handleSave());

        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, contact))); //delete button fires a delete event and passes the active contact.

        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        close.addClickShortcut(Key.ESCAPE);
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid())); //Validates the form every time it changes. If it is invalid, it disables the save button to avoid invalid submissions.

        return new HorizontalLayout(save, delete, close);
    }

    private void handleSave() {
        try {
            binder.writeBean(contact); //Write the form contents back to the original contact.
            fireEvent(new SaveEvent(this, contact)); //Fire a save event, so the parent component can handle the action.
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

    public void setContact(Contact contact) {
        this.contact = contact;
        //The binder.readBean -> populates the form fields with the input arg
        binder.readBean(contact); //to bind the values from the contact to the UI fields.
    }

    //define the new events
    //Create an abstract event which handles each event from a component of ContactForm component
    public static abstract class ContactFormEvent extends ComponentEvent<ContactForm> {

        private final Contact contact;

        protected ContactFormEvent(ContactForm source, Contact contact) {
            super(source, false);
            this.contact = contact;
        }

        public Contact getContact() {
            return contact;
        }
    }

    public static class SaveEvent extends ContactFormEvent {

        SaveEvent(ContactForm source, Contact contact) {
            super(source, contact);
        }
    }

    public static class DeleteEvent extends ContactFormEvent {

        DeleteEvent(ContactForm source, Contact contact) {
            super(source, contact);
        }
    }

    public static class CloseEvent extends ContactFormEvent {

        CloseEvent(ContactForm source) {
            super(source, null);
        }
    }

    //uses Vaadin’s event bus to register the custom event types
    @Override
    public <T extends ComponentEvent<?>>Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}
