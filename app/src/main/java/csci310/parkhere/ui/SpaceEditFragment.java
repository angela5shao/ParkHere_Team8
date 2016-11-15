package csci310.parkhere.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

import csci310.parkhere.R;
import csci310.parkhere.controller.ClientController;
import csci310.parkhere.resource.CarType;
import resource.MyEntry;
import resource.NetworkPackage;
import resource.ParkingSpot;
import resource.TimeInterval;
import resource.User;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SpaceEditFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SpaceEditFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SpaceEditFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private ParkingSpot thisParkingSpot;

    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 0;

    private Button mDoneButton, mUploadPicButton, mEditAddressButton;
    private EditText mAddressText, mDescriptionText;
    private ImageView mSpacePic;
    private Spinner mCartypeSpinner, mCancelPolicySpinner;

    private LatLng mCurrLocation;

    private OnFragmentInteractionListener mListener;

    public SpaceEditFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SpaceEditFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SpaceEditFragment newInstance(String param1, String param2) {
        SpaceEditFragment fragment = new SpaceEditFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            thisParkingSpot = (ParkingSpot) getArguments().getSerializable("spot");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_space_edit, container, false);

        mAddressText = (EditText) v.findViewById(R.id.address_text);
        mAddressText.setText(thisParkingSpot.getStreetAddr());
        mDescriptionText = (EditText) v.findViewById(R.id.description_text);
        mDescriptionText.setText(thisParkingSpot.getDescription());
        mSpacePic = (ImageView) v.findViewById(R.id.parkingSpotImage);
        // TODO: Set image of parking spot
        mCartypeSpinner = (Spinner)v.findViewById(R.id.editCartype_spinner);
        mCartypeSpinner.setSelection(thisParkingSpot.getCartype());
        mCancelPolicySpinner = (Spinner)v.findViewById(R.id.editCancelPolicy_spinner);
        mCancelPolicySpinner.setSelection(thisParkingSpot.cancelpolicy);

        mDoneButton = (Button)v.findViewById(R.id.editSpaceSave_btn);
        mUploadPicButton = (Button)v.findViewById(R.id.spacePicUpload_btn);
        mEditAddressButton = (Button)v.findViewById(R.id.changeAddress_btn);

        mDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAddressText.getText().length() == 0) {
                    Toast.makeText(getContext(), "Please enter address.", Toast.LENGTH_SHORT).show();
                    return;
                }

                EditSpaceTask editProfileTask = new EditSpaceTask(mAddressText.getText().toString(),
                        mDescriptionText.getText().toString(),
                        mCartypeSpinner.getSelectedItem().toString(),
                        mCancelPolicySpinner.getSelectedItem().toString(),
                        mSpacePic);
                editProfileTask.execute((Void)null);
            }
        });

        mUploadPicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            // TODO
            }
        });


        mEditAddressButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                try {
                    AutocompleteFilter typeFilter = new AutocompleteFilter.Builder().setCountry("US").build();

                    Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .setFilter(typeFilter)
                                    .build(getActivity());

                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);

                } catch (GooglePlayServicesRepairableException e) {
                } catch (GooglePlayServicesNotAvailableException e) {
                }
            }

        });

        // Inflate the layout for this fragment
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("ONACTIVITYRESULT", "START");
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getContext(), data);
                mAddressText.setText(place.getAddress());
                mCurrLocation = place.getLatLng();
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getContext(), data);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private class EditSpaceTask extends AsyncTask<Void, Void, ParkingSpot> {
        String address;
        String description;
        String cartype;
        int cancelpolicy;
        ImageView picture;

        EditSpaceTask(String addr, String description, String cartype, int inCancelPolicy, ImageView pic){
            this.address = addr;
            this.description = description;
            this.cartype = cartype;
            this.cancelpolicy = inCancelPolicy;
            this.picture = pic;
        }

        @Override
        protected ParkingSpot doInBackground(Void... params ){
            ClientController clientController = ClientController.getInstance();
            clientController.editParkingSpot(address, description, cartype, cancelpolicy, picture);
            NetworkPackage NP = clientController.checkReceived();
            MyEntry<String, Serializable> entry = NP.getCommand();
            String key = entry.getKey();
            Object value = entry.getValue();
            if(key.equals("EDITPARKINGSPOT")){
                ParkingSpot spot = (ParkingSpot)value;
                return spot;
            }
            return null;
        }

        @Override
        protected void onPostExecute(ParkingSpot spot) {

            if(spot!=null){

            } else{
                Toast.makeText(getContext(), "Error on edit space! Please try again.", Toast.LENGTH_SHORT).show();
                // back to parking spot detail
            }
        }
    }

}
