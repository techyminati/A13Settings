package com.google.android.settings.intelligence.libs.contextualcards;

import android.app.slice.SliceManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import com.android.settings.intelligence.ContextualCardProto$ContextualCard;
import com.android.settings.intelligence.ContextualCardProto$ContextualCardList;
/* loaded from: classes2.dex */
public abstract class ContextualCardProvider extends ContentProvider {
    @Nullable
    public abstract ContextualCardProto$ContextualCardList getContextualCards();

    @Override // android.content.ContentProvider
    public boolean onCreate() {
        return true;
    }

    @Override // android.content.ContentProvider
    public final Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        throw new UnsupportedOperationException("Query operation is not supported currently.");
    }

    @Override // android.content.ContentProvider
    public final String getType(Uri uri) {
        throw new UnsupportedOperationException("GetType operation is not supported currently.");
    }

    @Override // android.content.ContentProvider
    public final Uri insert(Uri uri, ContentValues contentValues) {
        throw new UnsupportedOperationException("Insert operation is not supported currently.");
    }

    @Override // android.content.ContentProvider
    public final int delete(Uri uri, String str, String[] strArr) {
        throw new UnsupportedOperationException("Delete operation not supported currently.");
    }

    @Override // android.content.ContentProvider
    public final int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        throw new UnsupportedOperationException("Update operation is not supported currently.");
    }

    @Override // android.content.ContentProvider
    public Bundle call(String str, String str2, Bundle bundle) {
        ContextualCardProto$ContextualCardList contextualCards;
        Bundle bundle2 = new Bundle();
        if ("getCardList".equals(str) && (contextualCards = getContextualCards()) != null) {
            bundle2.putByteArray("cardList", contextualCards.toByteArray());
            SliceManager sliceManager = (SliceManager) getContext().getSystemService(SliceManager.class);
            for (ContextualCardProto$ContextualCard contextualCardProto$ContextualCard : contextualCards.getCardList()) {
                sliceManager.grantSlicePermission("com.android.settings", Uri.parse(contextualCardProto$ContextualCard.getSliceUri()));
            }
        }
        return bundle2;
    }
}
