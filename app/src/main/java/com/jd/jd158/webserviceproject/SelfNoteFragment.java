package com.jd.jd158.webserviceproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SelfNoteFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SelfNoteFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class SelfNoteFragment extends Fragment {
    private View mRootView;
    private Button mSaveButton;
    private EditText mSubjectEditText, mBodyEditText;

    OutputStream output;


    public static final String LOG_TAG = "TAG";

    private EditText mName;
    private EditText mDob;
    private EditText mAddress;
    EditText mMobile;
    EditText mID;
    EditText mEmergencyContactName;
    EditText mEmergencyContactMobile;

    File myFile;

    public SelfNoteFragment() {
        // Required empty public constructor
    }

    public static SelfNoteFragment newInstance(){
        SelfNoteFragment fragment = new SelfNoteFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_self_note, container, false);
        mSaveButton = (Button) mRootView.findViewById(R.id.button_save);
        mName = (EditText)  mRootView.findViewById(R.id.name);
        mAddress = (EditText) mRootView.findViewById(R.id.address);
        mDob = (EditText)  mRootView.findViewById(R.id.dob);
        mMobile = (EditText)  mRootView.findViewById(R.id.mobile);
        mID = (EditText)  mRootView.findViewById(R.id.id);
        mEmergencyContactName = (EditText)  mRootView.findViewById(R.id.emergency_name);
        mEmergencyContactMobile = (EditText)  mRootView.findViewById(R.id.emergency_mobile);

        // Inflate the layout for this fragment
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (mName.getText().toString().isEmpty()){
                    mName.setError("Name is empty");
                    mName.requestFocus();
                    return;
                }

                if (mID.getText().toString().isEmpty()){
                    mID.setError("Name is empty");
                    mID.requestFocus();
                    return;
                }

                try {
                    createPdf();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
        return mRootView;

    }

    private void createPdf() throws FileNotFoundException, DocumentException, JSONException {
        File pdfFolder = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), "pdfdemo");
        if (!pdfFolder.exists()) {
            pdfFolder.mkdir();
            Log.i(LOG_TAG, "Pdf Directory created");
        }

        //Create time stamp
        Date date = new Date() ;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(date);

        myFile = new File(pdfFolder + timeStamp + ".pdf");

        OutputStream output = new FileOutputStream(myFile);

        //Step 1
        Document document = new Document();

        //Step 2
        PdfWriter.getInstance(document, output);

        //Step 3
        document.open();

        //Step 4 Add content
        String pdfName =  (getString(R.string.label_name) + " " +mName.getText().toString());
        String pdfDob =  (getString(R.string.label_dob) + " " +mDob.getText().toString());
        String pdfAddress =  (getString(R.string.label_address) + " " +mAddress.getText().toString());
        String pdfId =  (getString(R.string.label_id) + " " + mID.getText().toString());
        String pdfEmergencyContactName =  (getString(R.string.label_emergency_contact_name) + " " + mEmergencyContactName.getText().toString());
        String pdfEmergencyContactNumber =  (getString(R.string.label_emergency_contact_number) + " " + mEmergencyContactMobile.getText().toString());

        JSONObject json = makJsonObject(pdfName,
                                        pdfDob,
                                        pdfAddress,
                                        pdfId,
                                        pdfEmergencyContactName,
                                        pdfEmergencyContactNumber);

        String jsonString = json.toString();


        document.add(new Paragraph(jsonString));
        viewPdf();

        //Step 5: Close the document
        document.close();
    }

    private void viewPdf(){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(myFile), "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

    public JSONObject makJsonObject(String name,
                                    String dob,
                                    String address,
                                    String id,
                                    String emergencyContactName,
                                    String emergencyContactNumber)
            throws JSONException {
        JSONObject obj = null;
        JSONArray jsonArray = new JSONArray();
            obj = new JSONObject();
            try {
                obj.put("name", name);
                obj.put("dob", dob);
                obj.put("address", address);
                obj.put("id", id);
                obj.put("birthday", emergencyContactName);
                obj.put("emergencyContactNumber", emergencyContactNumber);

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            jsonArray.put(obj);


        JSONObject finalobject = new JSONObject();
        finalobject.put("pdfDetails", jsonArray);
        return finalobject;
    }


}
