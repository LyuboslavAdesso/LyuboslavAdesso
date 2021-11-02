import { format, parse } from "date-fns";

// @ts-ignore
window._setDatePickerFormats = (datePicker) => {

  datePicker.i18n.parseDate = (dateString) => {
    dateString = dateString.trim();

    let format = "dd.MM.yyyy";
    if (dateString.length === 10 && dateString.includes("/")) {
        format = "dd/MM/yyyy";
    } else if (dateString.length == 6) {
        format = "ddMMyy";
    }

    try {
        const parsed = parse(dateString, format, new Date());
        return {
            day: parsed.getDate(),
            month: parsed.getMonth(),
            year: parsed.getFullYear()
        };
    } catch (err) {
        return false;
    }
  };
};