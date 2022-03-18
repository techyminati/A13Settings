package com.android.settingslib.users;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import com.android.internal.util.UserIcons;
import com.android.settingslib.drawable.CircleFramedDrawable;
import com.android.settingslib.utils.ThreadUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
/* loaded from: classes.dex */
public class EditUserPhotoController {
    private final Activity mActivity;
    private final ActivityStarter mActivityStarter;
    private final String mFileAuthority;
    private final ImageView mImageView;
    private final File mImagesDir;
    private Bitmap mNewUserPhotoBitmap;
    private Drawable mNewUserPhotoDrawable;

    public EditUserPhotoController(Activity activity, ActivityStarter activityStarter, ImageView imageView, Bitmap bitmap, String str) {
        this.mActivity = activity;
        this.mActivityStarter = activityStarter;
        this.mFileAuthority = str;
        File file = new File(activity.getCacheDir(), "multi_user");
        this.mImagesDir = file;
        file.mkdir();
        this.mImageView = imageView;
        imageView.setOnClickListener(new View.OnClickListener() { // from class: com.android.settingslib.users.EditUserPhotoController$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                EditUserPhotoController.this.lambda$new$0(view);
            }
        });
        this.mNewUserPhotoBitmap = bitmap;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(View view) {
        showAvatarPicker();
    }

    public boolean onActivityResult(int i, int i2, Intent intent) {
        if (i2 == -1 && i == 1004) {
            if (intent.hasExtra("default_icon_tint_color")) {
                onDefaultIconSelected(intent.getIntExtra("default_icon_tint_color", -1));
                return true;
            } else if (intent.getData() != null) {
                onPhotoCropped(intent.getData());
                return true;
            }
        }
        return false;
    }

    public Drawable getNewUserPhotoDrawable() {
        return this.mNewUserPhotoDrawable;
    }

    private void showAvatarPicker() {
        Intent intent = new Intent(this.mImageView.getContext(), AvatarPickerActivity.class);
        intent.putExtra("file_authority", this.mFileAuthority);
        this.mActivityStarter.startActivityForResult(intent, 1004);
    }

    private void onDefaultIconSelected(final int i) {
        try {
            ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settingslib.users.EditUserPhotoController$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    EditUserPhotoController.this.lambda$onDefaultIconSelected$2(i);
                }
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e("EditUserPhotoController", "Error processing default icon", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onDefaultIconSelected$2(int i) {
        Resources resources = this.mActivity.getResources();
        final Bitmap convertToBitmapAtUserIconSize = UserIcons.convertToBitmapAtUserIconSize(resources, UserIcons.getDefaultUserIconInColor(resources, i));
        ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settingslib.users.EditUserPhotoController$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                EditUserPhotoController.this.lambda$onDefaultIconSelected$1(convertToBitmapAtUserIconSize);
            }
        });
    }

    private void onPhotoCropped(final Uri uri) {
        ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settingslib.users.EditUserPhotoController$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                EditUserPhotoController.this.lambda$onPhotoCropped$4(uri);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:17:0x0030  */
    /* JADX WARN: Removed duplicated region for block: B:31:0x003d A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:35:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$onPhotoCropped$4(android.net.Uri r6) {
        /*
            r5 = this;
            java.lang.String r0 = "Cannot close image stream"
            java.lang.String r1 = "EditUserPhotoController"
            r2 = 0
            android.app.Activity r3 = r5.mActivity     // Catch: all -> 0x0020, FileNotFoundException -> 0x0022
            android.content.ContentResolver r3 = r3.getContentResolver()     // Catch: all -> 0x0020, FileNotFoundException -> 0x0022
            java.io.InputStream r6 = r3.openInputStream(r6)     // Catch: all -> 0x0020, FileNotFoundException -> 0x0022
            android.graphics.Bitmap r2 = android.graphics.BitmapFactory.decodeStream(r6)     // Catch: FileNotFoundException -> 0x001e, all -> 0x0039
            if (r6 == 0) goto L_0x002e
            r6.close()     // Catch: IOException -> 0x0019
            goto L_0x002e
        L_0x0019:
            r6 = move-exception
            android.util.Log.w(r1, r0, r6)
            goto L_0x002e
        L_0x001e:
            r3 = move-exception
            goto L_0x0024
        L_0x0020:
            r5 = move-exception
            goto L_0x003b
        L_0x0022:
            r3 = move-exception
            r6 = r2
        L_0x0024:
            java.lang.String r4 = "Cannot find image file"
            android.util.Log.w(r1, r4, r3)     // Catch: all -> 0x0039
            if (r6 == 0) goto L_0x002e
            r6.close()     // Catch: IOException -> 0x0019
        L_0x002e:
            if (r2 == 0) goto L_0x0038
            com.android.settingslib.users.EditUserPhotoController$$ExternalSyntheticLambda3 r6 = new com.android.settingslib.users.EditUserPhotoController$$ExternalSyntheticLambda3
            r6.<init>()
            com.android.settingslib.utils.ThreadUtils.postOnMainThread(r6)
        L_0x0038:
            return
        L_0x0039:
            r5 = move-exception
            r2 = r6
        L_0x003b:
            if (r2 == 0) goto L_0x0045
            r2.close()     // Catch: IOException -> 0x0041
            goto L_0x0045
        L_0x0041:
            r6 = move-exception
            android.util.Log.w(r1, r0, r6)
        L_0x0045:
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settingslib.users.EditUserPhotoController.lambda$onPhotoCropped$4(android.net.Uri):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onPhotoProcessed */
    public void lambda$onPhotoCropped$3(Bitmap bitmap) {
        if (bitmap != null) {
            this.mNewUserPhotoBitmap = bitmap;
            CircleFramedDrawable instance = CircleFramedDrawable.getInstance(this.mImageView.getContext(), this.mNewUserPhotoBitmap);
            this.mNewUserPhotoDrawable = instance;
            this.mImageView.setImageDrawable(instance);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public File saveNewUserPhotoBitmap() {
        if (this.mNewUserPhotoBitmap == null) {
            return null;
        }
        try {
            File file = new File(this.mImagesDir, "NewUserPhoto.png");
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            this.mNewUserPhotoBitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            return file;
        } catch (IOException e) {
            Log.e("EditUserPhotoController", "Cannot create temp file", e);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Bitmap loadNewUserPhotoBitmap(File file) {
        return BitmapFactory.decodeFile(file.getAbsolutePath());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeNewUserPhotoBitmapFile() {
        new File(this.mImagesDir, "NewUserPhoto.png").delete();
    }
}
