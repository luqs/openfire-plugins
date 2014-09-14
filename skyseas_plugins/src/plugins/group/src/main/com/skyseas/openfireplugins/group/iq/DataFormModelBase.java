package com.skyseas.openfireplugins.group.iq;

import org.dom4j.Element;
import org.dom4j.QName;
import org.xmpp.forms.DataForm;
import org.xmpp.forms.FormField;

/**
 * Created by zhangzhi on 2014/9/9.
 */
public class DataFormModelBase {
    protected final DataForm queryForm;
    protected final Element element;

    public DataFormModelBase(Element element) {
        this.element = element;
        this.queryForm = new DataForm(element.element(QName.get("x", "jabber:x:data")));
    }

    protected int getIntegerFieldValue(String name, int defValue) {
        String value = getFieldValue(name);
        if(value != null) {
            try{
                return Integer.valueOf(value);
            }catch (NumberFormatException e){;}
        }
        return defValue;
    }

    protected String getFieldValue(String name) {
        FormField field = queryForm.getField(name);
        if(field != null) {
            return field.getFirstValue();
        }
        return null;
    }
}
