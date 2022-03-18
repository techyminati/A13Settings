package com.google.android.libraries.hats20.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;
/* loaded from: classes.dex */
public class EditTextWithImeDone extends EditText {
    public EditTextWithImeDone(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // android.widget.TextView, android.view.View
    public InputConnection onCreateInputConnection(EditorInfo editorInfo) {
        InputConnection onCreateInputConnection = super.onCreateInputConnection(editorInfo);
        editorInfo.imeOptions = ((editorInfo.imeOptions & (-256)) | 6) & (-1073741825);
        editorInfo.actionId = 6;
        return onCreateInputConnection;
    }
}
