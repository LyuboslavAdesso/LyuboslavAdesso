package com.example.application.views.list;

import com.example.application.data.entity.Contact;
import com.example.application.data.service.CrmService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.provider.ListDataProvider;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

@Ignore
public class ContactListViewTest {

    private ContactListView contactListView = new ContactListView(new CrmService(null, null, null));
    @Test
    public void init_shouldHaveValidState() {
        contactListView.hasClassName("list-view");

        //assert
        contactListView.getWidth();
        contactListView.getHeight();
    }

    @Test
    public void initView_shouldConfigureGridWithValidColumns() {
        List<Grid.Column<Contact>> columns = contactListView.contactTable.getColumns();
        //TODO: check keys or name generators
    }

    @Test
    public void selectSingleElementTable_shouldFireEditContactModeEvent() {
//        Set contact items inside the grid or populate them on initializing!!
        Grid<Contact> grid = contactListView.contactTable;
        Contact firstContact = ((ListDataProvider<Contact>) grid.getDataProvider()).getItems().iterator().next();

        ContactForm form = contactListView.contactForm;

        Assert.assertFalse(form.isVisible());
        grid.asSingleSelect().setValue(firstContact);
        Assert.assertTrue(form.isVisible());
        Assert.assertEquals(firstContact.getFirstName(), form.firstName.getValue());
        //TODO: make test case if selected contact is null -> should close form, otherwise -> the contact form should have as contact the selected one,, and the form should be visible

    }

    @Test
    public void initializingView_shouldPopulateTheTableFromTheService() {
        //getItems from the grid
    }

    @Test
    public void initializingView_shouldCloseTheEditor() {
        //the form should have as contact null (you can check the form fields if there are value) and the form should not be visible
//        contactListView.contactForm.isVisible()
    }

    @Test
    public void filterContacts_shouldWorkOk() {
        contactListView.contactFilter.setValue("asd"); //This fires the value change event of the filter -> It means can be test easily
    }

    @Test
    public void clickAddButton_shouldFireItsOwnSaveContactEvent() {

    }

    //TODO: in the Integration test -> When save or delete buttons are clicked it should be tested if the contacts are persisted or removed from the database!!!!
    @Test
    public void test_for_integration_tests() {
//        contactListView.contactForm.save.click();
//        check DB
    }
}
