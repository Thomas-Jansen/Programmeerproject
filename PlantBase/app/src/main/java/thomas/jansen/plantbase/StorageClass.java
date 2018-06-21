package thomas.jansen.plantbase;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.Date;

import static android.widget.Toast.LENGTH_LONG;
import static thomas.jansen.plantbase.AccountActivity.mAuth;

public class StorageClass {

    private StorageReference mStorageRef;
    private StorageReference riversRef;
    private Context context;
    private MyPlant myPlant;
    private Uri photo;
    public Uri photoAdded;
    private Callback callback;

    public interface Callback {
        void gotPhotoUri(Uri addedPhoto);
    }

     StorageClass(Context context, MyPlant myPlant, Callback callback) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        this.context = context;
        this.myPlant = myPlant;
        this.callback = callback;
    }

    public void StoreImage(byte[] photoTaken) {

        if (mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
        }
        Date currentDate = Calendar.getInstance().getTime();
        this.mStorageRef = FirebaseStorage.getInstance().getReference().child(mAuth.getUid())
                .child(String.valueOf(myPlant.getStartdate())).child(String.valueOf(currentDate+".jpg"));
        UploadTask uploadTask = mStorageRef.putBytes(photoTaken);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                String errorString = exception.getMessage();
                Toast.makeText(context, errorString, LENGTH_LONG).show();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                System.out.println("Photo added");
                mStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        System.out.println("Uri Added "+uri);
                        photoAdded = uri;
                        callback.gotPhotoUri(photoAdded);
                    }
                });
            }
        });
    }

    public void DeleteStoredPhoto(Uri path) {

        if (mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
        }
        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(String.valueOf(path));
        storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                Toast.makeText(context, "Image deleted", LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                String errorString = exception.getMessage();
                Toast.makeText(context, errorString, LENGTH_LONG).show();
            }
        });
    }


}
