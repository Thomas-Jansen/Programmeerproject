/*
    Thomas Jansen 11008938
    Programmeerproject - PlantBase

    Request added photos from FireBase storage.
*/

package thomas.jansen.plantbase.Requests;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.Date;

import thomas.jansen.plantbase.Classes.MyPlant;

import static android.widget.Toast.LENGTH_LONG;
import static thomas.jansen.plantbase.Activities.AccountActivity.mAuth;

public class StorageRequest {

    private StorageReference mStorageRef;
    private Context context;
    private MyPlant myPlant;
    private Uri photoAdded;
    private Callback callback;

    public interface Callback {
        void gotPhotoUri(Uri addedPhoto);
    }

     public StorageRequest(Context context, MyPlant myPlant, Callback callback) {

        this.context = context;
        this.myPlant = myPlant;
        this.callback = callback;
    }

    // Query.
    public void StoreImage(byte[] photoTaken) {

        if (mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
        }
        Date currentDate = Calendar.getInstance().getTime();
        this.mStorageRef = FirebaseStorage.getInstance().getReference().child(mAuth.getUid())
                .child(String.valueOf(myPlant.getStartdate())).child(String.valueOf(currentDate+".jpg"));
        UploadTask uploadTask = mStorageRef.putBytes(photoTaken);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                String errorString = exception.getMessage();
                Toast.makeText(context, errorString, LENGTH_LONG).show();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        photoAdded = uri;
                        callback.gotPhotoUri(photoAdded);
                    }
                });
            }
        });
    }

    // Delete query.
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
