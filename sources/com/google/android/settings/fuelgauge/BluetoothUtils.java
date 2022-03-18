package com.google.android.settings.fuelgauge;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import androidx.window.R;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import java.io.IOException;
import java.nio.ByteBuffer;
/* loaded from: classes2.dex */
final class BluetoothUtils {
    private static String emptyIfNull(String str) {
        return str == null ? "" : str;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ContentValues wrapBluetoothData(Context context, CachedBluetoothDevice cachedBluetoothDevice, boolean z) {
        BluetoothDevice device = cachedBluetoothDevice.getDevice();
        ContentValues contentValues = new ContentValues();
        contentValues.put("type", Integer.valueOf(device.getType()));
        contentValues.put("name", emptyIfNull(device.getName()));
        contentValues.put("alias", emptyIfNull(device.getAlias()));
        contentValues.put("address", emptyIfNull(device.getAddress()));
        contentValues.put("batteryLevel", Integer.valueOf(device.getBatteryLevel()));
        putStringMetadata(contentValues, "hardwareVersion", device.getMetadata(3));
        putStringMetadata(contentValues, "batteryLevelRight", device.getMetadata(11));
        putStringMetadata(contentValues, "batteryLevelLeft", device.getMetadata(10));
        putStringMetadata(contentValues, "batteryLevelCase", device.getMetadata(12));
        putStringMetadata(contentValues, "batteryChargingRight", device.getMetadata(14));
        putStringMetadata(contentValues, "batteryChargingLeft", device.getMetadata(13));
        putStringMetadata(contentValues, "batteryChargingCase", device.getMetadata(15));
        putStringMetadata(contentValues, "batteryChargingMain", device.getMetadata(19));
        if (z) {
            int dimensionPixelSize = context.getResources().getDimensionPixelSize(R.dimen.bluetooth_icon_size);
            putIconMetadata(context, contentValues, dimensionPixelSize, "deviceIconMain", device.getMetadata(5));
            putIconMetadata(context, contentValues, dimensionPixelSize, "deviceIconCase", device.getMetadata(9));
            putIconMetadata(context, contentValues, dimensionPixelSize, "deviceIconLeft", device.getMetadata(7));
            putIconMetadata(context, contentValues, dimensionPixelSize, "deviceIconRight", device.getMetadata(8));
        }
        BluetoothClass bluetoothClass = device.getBluetoothClass();
        if (bluetoothClass != null) {
            contentValues.put("bluetoothClass", marshall(bluetoothClass));
        }
        return contentValues;
    }

    static byte[] marshall(Parcelable parcelable) {
        Parcel obtain = Parcel.obtain();
        parcelable.writeToParcel(obtain, 0);
        byte[] marshall = obtain.marshall();
        obtain.recycle();
        return marshall;
    }

    private static void putStringMetadata(ContentValues contentValues, String str, byte[] bArr) {
        if (bArr != null && bArr.length != 0) {
            contentValues.put(str, new String(bArr));
        }
    }

    private static void putIconMetadata(Context context, ContentValues contentValues, int i, String str, byte[] bArr) {
        Bitmap icon = getIcon(context, bArr);
        if (icon != null) {
            if (icon.getWidth() != icon.getHeight()) {
                Log.w("BluetoothUtils", "putIconMetadata() invalid size for " + str);
                return;
            }
            if (icon.getWidth() > i) {
                icon = Bitmap.createScaledBitmap(icon, i, i, true);
            }
            ByteBuffer allocate = ByteBuffer.allocate(icon.getAllocationByteCount());
            String config = icon.getConfig().toString();
            icon.copyPixelsToBuffer(allocate);
            contentValues.put(str, allocate.array());
            contentValues.put(BluetoothContract.getIconSizeKey(str), Integer.valueOf(icon.getWidth()));
            contentValues.put(BluetoothContract.getIconConfigKey(str), config);
            Log.d("BluetoothUtils", String.format("putIconMetadata() for %s size=%d config=%s", str, Integer.valueOf(icon.getWidth()), config));
            icon.recycle();
        }
    }

    private static Bitmap getIcon(Context context, byte[] bArr) {
        if (!(bArr == null || bArr.length == 0)) {
            Uri parse = Uri.parse(new String(bArr));
            try {
                context.getContentResolver().takePersistableUriPermission(parse, 1);
                return MediaStore.Images.Media.getBitmap(context.getContentResolver(), parse);
            } catch (IOException e) {
                Log.e("BluetoothUtils", "failed to get bitmap for: " + parse, e);
            } catch (SecurityException e2) {
                Log.e("BluetoothUtils", "failed to take persistable permission for: " + parse, e2);
            }
        }
        return null;
    }
}
