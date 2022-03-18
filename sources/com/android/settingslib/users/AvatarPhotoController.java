package com.android.settingslib.users;

import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.util.EventLog;
import android.util.Log;
import androidx.core.content.FileProvider;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import libcore.io.Streams;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class AvatarPhotoController {
    private final AvatarPickerActivity mActivity;
    private final Uri mCropPictureUri;
    private final String mFileAuthority;
    private final File mImagesDir;
    private final int mPhotoSize;
    private final Uri mTakePictureUri;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AvatarPhotoController(AvatarPickerActivity avatarPickerActivity, boolean z, String str) {
        this.mActivity = avatarPickerActivity;
        this.mFileAuthority = str;
        File file = new File(avatarPickerActivity.getCacheDir(), "multi_user");
        this.mImagesDir = file;
        file.mkdir();
        this.mCropPictureUri = createTempImageUri(avatarPickerActivity, "CropEditUserPhoto.jpg", !z);
        this.mTakePictureUri = createTempImageUri(avatarPickerActivity, "TakeEditUserPhoto.jpg", !z);
        this.mPhotoSize = getPhotoSize(avatarPickerActivity);
    }

    public boolean onActivityResult(int i, int i2, Intent intent) {
        if (i2 != -1) {
            return false;
        }
        Uri data = (intent == null || intent.getData() == null) ? this.mTakePictureUri : intent.getData();
        if (!"content".equals(data.getScheme())) {
            Log.e("AvatarPhotoController", "Invalid pictureUri scheme: " + data.getScheme());
            EventLog.writeEvent(1397638484, "172939189", -1, data.getPath());
            return false;
        }
        switch (i) {
            case 1001:
            case 1002:
                if (!this.mTakePictureUri.equals(data)) {
                    copyAndCropPhoto(data);
                } else if (PhotoCapabilityUtils.canCropPhoto(this.mActivity)) {
                    cropPhoto();
                } else {
                    onPhotoNotCropped(data);
                }
                return true;
            case 1003:
                this.mActivity.returnUriResult(data);
                return true;
            default:
                return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void takePhoto() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE_SECURE");
        appendOutputExtra(intent, this.mTakePictureUri);
        this.mActivity.startActivityForResult(intent, 1002);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void choosePhoto() {
        Intent intent = new Intent("android.provider.action.PICK_IMAGES", (Uri) null);
        intent.setType("image/*");
        this.mActivity.startActivityForResult(intent, 1001);
    }

    private void copyAndCropPhoto(final Uri uri) {
        new AsyncTask<Void, Void, Void>() { // from class: com.android.settingslib.users.AvatarPhotoController.1
            /* JADX INFO: Access modifiers changed from: protected */
            public Void doInBackground(Void... voidArr) {
                ContentResolver contentResolver = AvatarPhotoController.this.mActivity.getContentResolver();
                try {
                    InputStream openInputStream = contentResolver.openInputStream(uri);
                    OutputStream openOutputStream = contentResolver.openOutputStream(AvatarPhotoController.this.mTakePictureUri);
                    Streams.copy(openInputStream, openOutputStream);
                    if (openOutputStream != null) {
                        openOutputStream.close();
                    }
                    if (openInputStream == null) {
                        return null;
                    }
                    openInputStream.close();
                    return null;
                } catch (IOException e) {
                    Log.w("AvatarPhotoController", "Failed to copy photo", e);
                    return null;
                }
            }

            /* JADX INFO: Access modifiers changed from: protected */
            public void onPostExecute(Void r1) {
                if (!AvatarPhotoController.this.mActivity.isFinishing() && !AvatarPhotoController.this.mActivity.isDestroyed()) {
                    AvatarPhotoController.this.cropPhoto();
                }
            }
        }.execute(new Void[0]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cropPhoto() {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(this.mTakePictureUri, "image/*");
        appendOutputExtra(intent, this.mCropPictureUri);
        appendCropExtras(intent);
        if (intent.resolveActivity(this.mActivity.getPackageManager()) != null) {
            try {
                StrictMode.disableDeathOnFileUriExposure();
                this.mActivity.startActivityForResult(intent, 1003);
            } finally {
                StrictMode.enableDeathOnFileUriExposure();
            }
        } else {
            onPhotoNotCropped(this.mTakePictureUri);
        }
    }

    private void appendOutputExtra(Intent intent, Uri uri) {
        intent.putExtra("output", uri);
        intent.addFlags(3);
        intent.setClipData(ClipData.newRawUri("output", uri));
    }

    private void appendCropExtras(Intent intent) {
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);
        intent.putExtra("scaleUpIfNeeded", true);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", this.mPhotoSize);
        intent.putExtra("outputY", this.mPhotoSize);
    }

    private void onPhotoNotCropped(final Uri uri) {
        new AsyncTask<Void, Void, Bitmap>() { // from class: com.android.settingslib.users.AvatarPhotoController.2
            /* JADX INFO: Access modifiers changed from: protected */
            public Bitmap doInBackground(Void... voidArr) {
                Bitmap createBitmap = Bitmap.createBitmap(AvatarPhotoController.this.mPhotoSize, AvatarPhotoController.this.mPhotoSize, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(createBitmap);
                try {
                    Bitmap decodeStream = BitmapFactory.decodeStream(AvatarPhotoController.this.mActivity.getContentResolver().openInputStream(uri));
                    if (decodeStream != null) {
                        AvatarPhotoController avatarPhotoController = AvatarPhotoController.this;
                        int rotation = avatarPhotoController.getRotation(avatarPhotoController.mActivity, uri);
                        int min = Math.min(decodeStream.getWidth(), decodeStream.getHeight());
                        int width = (decodeStream.getWidth() - min) / 2;
                        int height = (decodeStream.getHeight() - min) / 2;
                        Matrix matrix = new Matrix();
                        matrix.setRectToRect(new RectF(width, height, width + min, height + min), new RectF(0.0f, 0.0f, AvatarPhotoController.this.mPhotoSize, AvatarPhotoController.this.mPhotoSize), Matrix.ScaleToFit.CENTER);
                        matrix.postRotate(rotation, AvatarPhotoController.this.mPhotoSize / 2.0f, AvatarPhotoController.this.mPhotoSize / 2.0f);
                        canvas.drawBitmap(decodeStream, matrix, new Paint());
                        return createBitmap;
                    }
                } catch (FileNotFoundException unused) {
                }
                return null;
            }

            /* JADX INFO: Access modifiers changed from: protected */
            public void onPostExecute(Bitmap bitmap) {
                AvatarPhotoController.this.saveBitmapToFile(bitmap, new File(AvatarPhotoController.this.mImagesDir, "CropEditUserPhoto.jpg"));
                AvatarPhotoController.this.mActivity.returnUriResult(AvatarPhotoController.this.mCropPictureUri);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getRotation(Context context, Uri uri) {
        int i = -1;
        try {
            i = new ExifInterface(context.getContentResolver().openInputStream(uri)).getAttributeInt("Orientation", -1);
        } catch (IOException e) {
            Log.e("AvatarPhotoController", "Error while getting rotation", e);
        }
        if (i == 3) {
            return 180;
        }
        if (i != 6) {
            return i != 8 ? 0 : 270;
        }
        return 90;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void saveBitmapToFile(Bitmap bitmap, File file) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            Log.e("AvatarPhotoController", "Cannot create temp file", e);
        }
    }

    private static int getPhotoSize(Context context) {
        Cursor query = context.getContentResolver().query(ContactsContract.DisplayPhoto.CONTENT_MAX_DIMENSIONS_URI, new String[]{"display_max_dim"}, null, null, null);
        if (query != null) {
            try {
                query.moveToFirst();
                int i = query.getInt(0);
                query.close();
                return i;
            } catch (Throwable th) {
                try {
                    query.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            }
        } else {
            if (query != null) {
                query.close();
            }
            return 500;
        }
    }

    private Uri createTempImageUri(Context context, String str, boolean z) {
        File file = new File(this.mImagesDir, str);
        if (z) {
            file.delete();
        }
        return FileProvider.getUriForFile(context, this.mFileAuthority, file);
    }
}
