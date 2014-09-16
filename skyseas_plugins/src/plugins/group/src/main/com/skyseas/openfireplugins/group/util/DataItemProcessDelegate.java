package com.skyseas.openfireplugins.group.util;

import org.xmpp.forms.DataForm;

/**
 * Created by apple on 14-9-14.
 */
public interface DataItemProcessDelegate<T>{
    <D extends T> Object  getPrimaryProperty(D dataItem);
    void beforeProcess(DataForm form);
    <D extends T> void  process(DataForm form, D dataItem);
}
