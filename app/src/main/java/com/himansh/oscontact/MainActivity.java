package com.himansh.oscontact;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {

    static EditText dob;
    static final int WRITE_CONTACTS_REQUEST_CODE = 555;
    static final int READ_CONTACTS_REQUEST_CODE=786;
    static final int REQUEST_IMAGE_CAPTURE = 989;
    static final int REQUEST_IMAGE_PICK = 9800;
    String mCurrentPhotoPath;
    DialogFragment dialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dob=(EditText)findViewById(R.id.dob);
        dob.setKeyListener(null);
        dob.setFocusable(false);
        setUpUneditableTextView((AutoCompleteTextView)findViewById(R.id.company));
        setUpUneditableTextView((AutoCompleteTextView)findViewById(R.id.cognizant));
        setUpUneditableTextView((AutoCompleteTextView)findViewById(R.id.title));
        setUpAutoCompleteTextView((AutoCompleteTextView)findViewById(R.id.month),getResources().getStringArray(R.array.months));
        int startYear=2016;
        int currentYear=Calendar.getInstance().get(Calendar.YEAR);
        String[] years=new String[currentYear-startYear+1];
        for(int i=0;i<years.length;i++){
            years[i]=String.valueOf(startYear++);
        }
        setUpAutoCompleteTextView((AutoCompleteTextView)findViewById(R.id.year),years);
        ((CheckBox)findViewById(R.id.use_primary_number)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                TextInputLayout textInputLayout=(TextInputLayout)findViewById(R.id.whatsapp_mobile_container);
                if(isChecked){
                    textInputLayout.setVisibility(View.GONE);
                }else{
                    textInputLayout.setVisibility(View.VISIBLE);
                }
                textInputLayout.getEditText().setText("");
            }
        });
    }
    public void setUpUneditableTextView(final AutoCompleteTextView autoCompleteTextView){
        autoCompleteTextView.setKeyListener(null);
        autoCompleteTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    Utility.hideSoftKeyboard(MainActivity.this);
                }
            }
        });
    }
    public void setUpAutoCompleteTextView(final AutoCompleteTextView autoCompleteTextView, String[] array){
        autoCompleteTextView.setKeyListener(null);
        autoCompleteTextView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,array));
        autoCompleteTextView.setFocusable(false);
        autoCompleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoCompleteTextView.showDropDown();
            }
        });
    }
    public void showImageDialog(View view){
        dialogFragment=new AddPhotoDialogFragment();
        dialogFragment.show(getSupportFragmentManager(),"photoPicker");
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
    public void takePicture(View view){
        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            Toast.makeText(this,"Camera Not Found", Toast.LENGTH_LONG).show();
            return;
        }
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.android.osfileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                dialogFragment.getDialog().cancel();
            }
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_IMAGE_CAPTURE:
                if (resultCode == RESULT_OK) {
                    ((ImageView)findViewById(R.id.picture)).setImageURI(Uri.fromFile(new File(mCurrentPhotoPath)));
                }
                break;
            case REQUEST_IMAGE_PICK:
                if(resultCode==RESULT_OK){
                    ((ImageView)findViewById(R.id.picture)).setImageURI(data.getData());
                }
        }
    }
    public void pickFromGallery(View view){
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,REQUEST_IMAGE_PICK);
        dialogFragment.getDialog().cancel();
    }
    public void selectDob(View view){
        view.setFocusable(true);
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }
    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd/MM/yyyy");
            DatePickerDialog datePickerDialog= new DatePickerDialog(getActivity(), this, year, month, day);
            return datePickerDialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            String date=String.format("%02d/%02d/%04d",day,month+1,year);
            dob.setText(date);
        }
    }
    public Contact getContact(){
        Contact contact=new Contact();
        contact.setName(((TextInputEditText)findViewById(R.id.name)).getText().toString());
        try {
            contact.setDob(new SimpleDateFormat("dd/MM/yyyy").parse(((TextInputEditText)findViewById(R.id.dob)).getText().toString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        contact.setMobile(((TextInputEditText)findViewById(R.id.mobile)).getText().toString());
        if(!((CheckBox)findViewById(R.id.use_primary_number)).isChecked()){
            contact.setWhatsappMobile(((TextInputEditText)findViewById(R.id.whatsapp_mobile)).getText().toString());
        }
        contact.setCompany(getString(R.string.cognizant));
        contact.setTitle(getString(R.string.orbit_shifters)+" - "+((AutoCompleteTextView)findViewById(R.id.month)).getText()+" "+((AutoCompleteTextView)findViewById(R.id.year)).getText());
        contact.setWorkEmail(((TextInputEditText)findViewById(R.id.work_email)).getText()+"@"+getString(R.string.cognizant_com));
        contact.setPersonalEmail(((TextInputEditText)findViewById(R.id.personal_email)).getText().toString());
        contact.setImage(((BitmapDrawable)((CircleImageView)findViewById(R.id.picture)).getDrawable()).getBitmap());
        return contact;
    }
    public boolean validateFields(){
        boolean result=true;
        if(((TextInputEditText)findViewById(R.id.name)).getText().toString().isEmpty() || !Pattern.compile("^[A-Za-z ,.'-]+$").matcher(((TextInputEditText)findViewById(R.id.name)).getText().toString()).matches()){
            ((TextInputLayout)findViewById(R.id.name_container)).setError("Invalid Name");
            result = false;
        }else{
            ((TextInputLayout)findViewById(R.id.name_container)).setErrorEnabled(false);
        }
        if(((TextInputEditText)findViewById(R.id.dob)).getText().toString().isEmpty()){
            ((TextInputLayout)findViewById(R.id.dob_container)).setError("Please select Date of Birth");
            result = false;
        }else {
            ((TextInputLayout)findViewById(R.id.dob_container)).setErrorEnabled(false);
        }
        if(((TextInputEditText)findViewById(R.id.mobile)).getText().toString().isEmpty() || !Patterns.PHONE.matcher(((TextInputEditText)findViewById(R.id.mobile)).getText().toString()).matches()){
            ((TextInputLayout)findViewById(R.id.mobile_container)).setError("Invalid Mobile Number");
            result = false;
        }else {
            ((TextInputLayout)findViewById(R.id.mobile_container)).setErrorEnabled(false);
        }
        if(!(((CheckBox)findViewById(R.id.use_primary_number)).isChecked()) && (((TextInputEditText)findViewById(R.id.whatsapp_mobile)).getText().toString().isEmpty() || !Patterns.PHONE.matcher(((TextInputEditText)findViewById(R.id.whatsapp_mobile)).getText().toString()).matches())){
            ((TextInputLayout)findViewById(R.id.whatsapp_mobile_container)).setError("Invalid Mobile Number");
            result = false;
        }else {
            ((TextInputLayout)findViewById(R.id.whatsapp_mobile_container)).setErrorEnabled(false);
        }
        if(((AutoCompleteTextView)findViewById(R.id.month)).getText().toString().isEmpty()){
            ((TextInputLayout)findViewById(R.id.month_container)).setError("Please select Month of Joining");
            result = false;
        }else {
            ((TextInputLayout)findViewById(R.id.month_container)).setErrorEnabled(false);
        }
        if(((AutoCompleteTextView)findViewById(R.id.year)).getText().toString().isEmpty()){
            ((TextInputLayout)findViewById(R.id.year_container)).setError("Please select Year of Joining");
            result = false;
        }else {
            ((TextInputLayout)findViewById(R.id.year_container)).setErrorEnabled(false);
        }
        if(((TextInputEditText)findViewById(R.id.work_email)).getText().toString().isEmpty() || !Pattern.compile("^[a-zA-Z0-9.]+$").matcher(((TextInputEditText)findViewById(R.id.work_email)).getText().toString()).matches()){
            ((TextInputLayout)findViewById(R.id.work_email_container)).setError("Invalid Work Email");
            result = false;
        }else {
            ((TextInputLayout)findViewById(R.id.work_email_container)).setErrorEnabled(false);
        }
        if(((TextInputEditText)findViewById(R.id.personal_email)).getText().toString().isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(((TextInputEditText)findViewById(R.id.personal_email)).getText().toString()).matches()){
            ((TextInputLayout)findViewById(R.id.personal_email_container)).setError("Invalid Personal Email");
            result = false;
        }else {
            ((TextInputLayout)findViewById(R.id.personal_email_container)).setErrorEnabled(false);
        }
        return result;
    }
    public void createContact(View view){
        if(!validateFields()){
            return;
        }
        Contact contact=getContact();
        if(Utility.getPermission(this, Manifest.permission.WRITE_CONTACTS,"WRITE_CONTACTS Permission is required to Create Contact Card",WRITE_CONTACTS_REQUEST_CODE)){
            new CreateContactTask().execute(contact);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch (requestCode){
            case WRITE_CONTACTS_REQUEST_CODE:
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    new CreateContactTask().execute();
                } else {
                    Utility.getAlertDialog(this,"Permission Denied","Permission to Write Device Contacts was not granted",null);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                sleep(5000);
                                finish();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            case READ_CONTACTS_REQUEST_CODE:
                if(grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    viewContact(getContactUri());
                } else {
                    Utility.getAlertDialog(this,"Permission Denied","Permission to Read Device Contacts was not granted",null);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                sleep(5000);
                                finish();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
        }
    }
    public class CreateContactTask extends AsyncTask<Contact,Void,Void> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog=new ProgressDialog(MainActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Creating contact card...");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Contact... params) {
            Log.i("contact",params[0].toString());
            ArrayList <ContentProviderOperation> ops = new ArrayList< ContentProviderOperation >();

            ops.add(ContentProviderOperation.newInsert(
                    ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                    .build());

            //------------------------------------------------------ Names
            if (params[0].getName() != null) {
                ops.add(ContentProviderOperation.newInsert(
                        ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                        .withValue(
                                ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                                params[0].getName()).build());
            }

            //------------------------------------------------------ Date of Birth

            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Event.START_DATE, new SimpleDateFormat("yyyy-MM-dd").format(params[0].getDob()))
                    .withValue(ContactsContract.CommonDataKinds.Event.TYPE, ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY)
                    .build());

            //------------------------------------------------------ Mobile Number
            if (params[0].getMobile() != null) {
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, params[0].getMobile())
                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                        .build());
            }
            //------------------------------------------------------ Whatsapp Mobile Number
            if (params[0].getWhatsappMobile() != null) {
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, params[0].getWhatsappMobile())
                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM)
                        .withValue(ContactsContract.CommonDataKinds.Phone.LABEL, "Whatsapp Mobile Number").build());
            }

            //------------------------------------------------------ Work Email
            if (params[0].getWorkEmail() != null) {
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Email.DATA, params[0].getWorkEmail())
                        .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                        .build());
            }

            //------------------------------------------------------ Personal Email
            if (params[0].getPersonalEmail() != null) {
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Email.DATA, params[0].getPersonalEmail())
                        .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_HOME)
                        .build());
            }
            //------------------------------------------------------ Organization
            if (!params[0].getCompany().equals("") && !params[0].getTitle().equals("")) {
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Organization.COMPANY, params[0].getCompany())
                        .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
                        .withValue(ContactsContract.CommonDataKinds.Organization.TITLE, params[0].getTitle())
                        .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
                        .build());
            }

            //------------------------------------------------------- Image
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            if(params[0].getImage()!=null) {// If an image is selected successfully
                params[0].getImage().compress(Bitmap.CompressFormat.PNG, 75, stream);

                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.IS_SUPER_PRIMARY, 1)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, stream.toByteArray())
                        .build());
                try {
                    stream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // Asking the Contact provider to create a new contact
            try {
                getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            progressDialog.dismiss();
            Toast.makeText(MainActivity.this,"Your Contact Card has been created",Toast.LENGTH_LONG).show();
            if(Utility.getPermission(MainActivity.this, Manifest.permission.READ_CONTACTS,"READ_CONTACTS Permission is required to Create Contact Card",READ_CONTACTS_REQUEST_CODE)){
                viewContact(getContactUri());
            }
        }
    }
    public void viewContact(Uri contactUri) {
        if(contactUri==null){
            Toast.makeText(this,"Contact URI is null",Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, contactUri);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
    public Uri getContactUri(){
        String[] PROJECTION = {ContactsContract.Contacts._ID, ContactsContract.Contacts.LOOKUP_KEY};
        String SELECTION = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                        ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " = ?" :
                        ContactsContract.Contacts.DISPLAY_NAME + " = ?";
        String[] selectionArgs= {((TextInputEditText)findViewById(R.id.name)).getText().toString()};

        Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, PROJECTION, SELECTION, selectionArgs, null);
        Uri uri=null;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                long contactId = cursor.getLong(0);
                String contactKey = cursor.getString(1);
                uri = ContactsContract.Contacts.getLookupUri(contactId, contactKey);
            }
        }
        return uri;
    }
}
