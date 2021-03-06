package csci310.parkhere.ui.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nostra13.universalimageloader.utils.L;

import junit.framework.Assert;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import csci310.parkhere.R;
import csci310.parkhere.controller.ClientController;
import csci310.parkhere.ui.activities.PaymentActivity;
import csci310.parkhere.ui.activities.RenterActivity;
import resource.MyEntry;
import resource.NetworkPackage;
import resource.ParkingSpot;
import resource.Review;
import resource.Time;
import resource.TimeInterval;
import resource.User;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchSpaceDetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchSpaceDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchSpaceDetailFragment extends Fragment implements OnMapReadyCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "position";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";
    private static final String ARG_PARAM5 = "param5";
    private static final String ARG_PARAM6 = "spot_images";
    View mView;

    // TODO: Rename and change types of parameters
    private int mPosition;
    private String mParam2;
    private String mParam3;
    private String mParam4;
    private String mParam5;
    private ArrayList<String> mImagesURLs;
    private GalleryPagerAdapter mImageAdapter;

    private ParkingSpot mParkingSpot;
    private OnFragmentInteractionListener mListener;

    ViewPager _pager;
    LinearLayout _thumbnails;
    Button _searchspacedetail_reservebutton;

    SupportMapFragment mMapView;
    private GoogleMap googleMap;
    CameraPosition cameraPosition;

    Time displaySearchStartTime;
    Time displaySearchEndTime;

    ListView spotReviewList;
    ArrayAdapter spotReivewListAdapter;
    ArrayList<String> spotReviewStringList;


    public SearchSpaceDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchSpaceDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchSpaceDetailFragment newInstance(String param1, String param2) {
        SearchSpaceDetailFragment fragment = new SearchSpaceDetailFragment();
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
            mPosition = getArguments().getInt(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            mParam3 = getArguments().getString(ARG_PARAM3);
            mParam4 = getArguments().getString(ARG_PARAM4);
            mParam5 = getArguments().getString(ARG_PARAM5);
            Log.d("FinalBattle", mParam2+" "+mParam3+" "+mParam4+" "+mParam5);

            mImagesURLs = getArguments().getStringArrayList(ARG_PARAM6);

            // Get ParkingSpot from controller
            ClientController controller = ClientController.getInstance();
            mParkingSpot = controller.searchResults.searchResultList.get(mPosition);
//
//            LoadSpotImageTask LSIT = new LoadSpotImageTask(mParkingSpot.getParkingSpotID());
//            LSIT.execute((Void) null);

//            LoadSpotImageTask LSIT = new LoadSpotImageTask(mParkingSpot.getParkingSpotID());
//            LSIT.execute((Void) null);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_search_space_detail, container, false);

        String[] startDate1 = mParam2.split("\\D");
        String[] endDate1 = mParam4.split("\\D");
        String startDateStr = startDate1[1] + "-" + startDate1[0] + "-" + startDate1[2];
        String endDateStr = endDate1[1] + "-" + endDate1[0] + "-" + endDate1[2];

        displaySearchStartTime = new Time(startDateStr + " " + mParam3 + "-0");
        displaySearchEndTime = new Time(endDateStr + " " + mParam5 + "-0");

        displaySearchStartTime.month += 1;
        displaySearchEndTime.month += 1;


        spotReviewList = (ListView) mView.findViewById(R.id.spot_review_list);

        _searchspacedetail_reservebutton = (Button) mView.findViewById(R.id.searchspacedetail_reservebutton);
        _searchspacedetail_reservebutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {


                System.out.println(mParam2 + " " + mParam3 + " " + mParam4 + " " + mParam5);
                String[] startDate1 = mParam2.split("\\D");
                String[] endDate1 = mParam4.split("\\D");
                String startDateStr = startDate1[1] + "-" + startDate1[0] + "-" + startDate1[2];
                String endDateStr = endDate1[1] + "-" + endDate1[0] + "-" + endDate1[2];

                Time startTime = new Time(startDateStr + " " + mParam3 + "-0");
                Time endTime = new Time(endDateStr + " " + mParam5 + "-0");
                TimeInterval timeInterval = new TimeInterval(startTime, endTime);
                Intent intent = new Intent(getContext(), PaymentActivity.class);
                intent.putExtra("USERID", ClientController.getInstance().getUser().userID);
                intent.putExtra("PARKINGSPOTID", mParkingSpot.getParkingSpotID());
                intent.putExtra("TIMEINTERVALSTART", timeInterval.startTime.toString());
                intent.putExtra("TIMEINTERVALEND", timeInterval.endTime.toString());
                intent.putExtra("PROVIDERID", mParkingSpot.getOwner());
                startActivityForResult(intent, 11);
            }
        });


        mMapView = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));
        mMapView.onCreate(savedInstanceState);


        mMapView.onResume();// needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(this);


        Log.d("MAPPOSITION", String.valueOf(mParkingSpot.getLat()) + " " + String.valueOf(mParkingSpot.getLon()));
        Log.d("MAPPOSITION", "googlemap = null?" + String.valueOf(googleMap == null));

        // Populate fields with data
        ((TextView) mView.findViewById(R.id.searchspacedetail_address)).setText(mParkingSpot.getStreetAddr());
        ((TextView) mView.findViewById(R.id.searchspacedetail_price)).setText(new Double(mParkingSpot.search_price).toString());
//        ((TextView) mView.findViewById(R.id.searchspacedetail_rating)).setText(new Double(mParkingSpot.rating).toString());
        TextView _provider = (TextView) mView.findViewById(R.id.searchspacedetail_providername);
        _provider.setText(new Long(mParkingSpot.getOwner()).toString());
        _provider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetPublicProfileTask GPPT = new GetPublicProfileTask(mParkingSpot.getOwner());
                GPPT.execute((Void) null);
            }
        });

        Time sTime = displaySearchStartTime;
        Time eTime = displaySearchEndTime;
        ((TextView) mView.findViewById(R.id.searchspacedetail_starttime)).setText(sTime.toString());
        ((TextView) mView.findViewById(R.id.searchspacedetail_endtime)).setText(eTime.toString());

        int carType = new Integer(mParkingSpot.getCartype());
        if(carType == 0)
        {
            ((TextView) mView.findViewById(R.id.searchspacedetail_cartype)).setText("Compact");
        }
        else if(carType == 1)
        {
            ((TextView) mView.findViewById(R.id.searchspacedetail_cartype)).setText("Covered parking");
        }
        else if(carType == 2)
        {
            ((TextView) mView.findViewById(R.id.searchspacedetail_cartype)).setText("Handicap");
        }
        else if(carType == 3)
        {
            ((TextView) mView.findViewById(R.id.searchspacedetail_cartype)).setText("SUV");
        }
        else if(carType == 4)
        {
            ((TextView) mView.findViewById(R.id.searchspacedetail_cartype)).setText("Truck");
        }
        else if(carType == 5)
        {
            ((TextView) mView.findViewById(R.id.searchspacedetail_cartype)).setText("AnyCarType");
        }


        int cancelPolity = new Integer(mParkingSpot.cancelpolicy);
        if (cancelPolity == 0)
        {
            ((TextView) mView.findViewById(R.id.searchspacedetail_cancelpolicy)).setText("Free cancellation");
        }
        else if(cancelPolity == 1)
        {
            ((TextView) mView.findViewById(R.id.searchspacedetail_cancelpolicy)).setText("50% Refund");
        }
        else if(cancelPolity == 2)
        {
            ((TextView) mView.findViewById(R.id.searchspacedetail_cancelpolicy)).setText("15% Refund");
        }


        ((TextView) mView.findViewById(R.id.searchspacedetail_rating)).setText(String.valueOf(mParkingSpot.review));



        //Display parking spot images from URLs
        _pager = (ViewPager) mView.findViewById(R.id.pager);
        _thumbnails = (LinearLayout) mView.findViewById(R.id.thumbnails);
        //Assert.assertNotNull() methods checks that the object is null or not.
        //      If it is null then it throws an AssertionError.
        Assert.assertNotNull(mImagesURLs);
        mImageAdapter = new GalleryPagerAdapter(getContext());
        _pager.setAdapter(mImageAdapter);
        _pager.setOffscreenPageLimit(6); // how many images to load into memory





        spotReviewStringList = new ArrayList<>();

        spotReivewListAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1,  spotReviewStringList);
        spotReviewList.setAdapter(spotReivewListAdapter);


        RequestReviewTask rrt = new RequestReviewTask(mParkingSpot.getParkingSpotID());
        rrt.execute();

        return mView;
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

    @Override
    public void onMapReady(GoogleMap inGoogleMap) {

        Log.d("ONMAPREADY", "inGoogleMap");

        googleMap = inGoogleMap;
        cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(mParkingSpot.getLat(), mParkingSpot.getLon())).zoom(12).build();
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(mParkingSpot.getLat(), mParkingSpot.getLon())));
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
    }

    // For mImageAdapter - Image horizontal scroll
    //      Source: https://github.com/sourcey/imagegallerydemo
    class GalleryPagerAdapter extends PagerAdapter {
        Context _context;
        LayoutInflater _inflater;

        public GalleryPagerAdapter(Context context) {
            _context = context;
            _inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return mImagesURLs.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((LinearLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View itemView = _inflater.inflate(R.layout.pager_gallery_item, container, false);
            container.addView(itemView);

            // Get the border size to show around each image
            int borderSize = _thumbnails.getPaddingTop();

            // Get the size of the actual thumbnail image
            int thumbnailSize = ((FrameLayout.LayoutParams)
                    _pager.getLayoutParams()).bottomMargin - (borderSize);

            // Set the thumbnail layout parameters. Adjust as required
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(thumbnailSize, thumbnailSize);
            params.setMargins(0, 0, borderSize, 0);

            final ImageView thumbView = new ImageView(_context);
            thumbView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            thumbView.setLayoutParams(params);
            thumbView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("Search Space Detail ", "- Thumbnail clicked @"+position);

                    // Set the pager position when thumbnail clicked
                    _pager.setCurrentItem(position);
                }
            });
            _thumbnails.addView(thumbView);

            // Source: https://github.com/davemorrissey/subsampling-scale-image-view
            final SubsamplingScaleImageView imageView =
                    (SubsamplingScaleImageView) itemView.findViewById(R.id.image);

            // Asynchronously load the image and set the thumbnail and pager view
            Glide.with(_context)
                    .load(mImagesURLs.get(position))
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                            imageView.setImage(ImageSource.bitmap(bitmap));
                            thumbView.setImageBitmap(bitmap);
                        }
                    });

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
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



// private class RenterReserveTask extends AsyncTask<Void, Void, Boolean> {
//        private final long parkingSpotID;
//        private final TimeInterval timeInterval;
//        private final long providerID;
//        private final long userID;
//
//        private ProgressDialog progressDialog;
//
//        RenterReserveTask(long parkingSpotID, TimeInterval timeinterval, long providerID, long userID){
//            this.parkingSpotID = parkingSpotID;
//            this.timeInterval = timeinterval;
//            this.providerID = providerID;
//            this.userID = userID;
//        }
//
//        @Override
//        protected void onPreExecute(){
//            //Display a progress dialog
//            progressDialog = new ProgressDialog(getContext(), R.style.AppTheme);
//            progressDialog.setIndeterminate(true);
//            progressDialog.setMessage("Booking...");
//            progressDialog.show();
//        }
//        @Override
//        protected Boolean doInBackground(Void... params ){
//            // call client controller
//            // call client controller
//            ClientController controller = ClientController.getInstance();
//            Intent intent = new Intent(getContext(), PaymentActivity.class);
//            intent.putExtra("USERID", userID);
//            intent.putExtra("PARKINGSPOTID", parkingSpotID);
//            intent.putExtra("TIMEINTERVALSTART", timeInterval.startTime.toString());
//            intent.putExtra("TIMEINTERVALEND", timeInterval.endTime.toString());
//            intent.putExtra("PROVIDERID", providerID);
//            startActivityForResult(intent, 11);
//
//
//
//
//            controller.renterReserve(userID, parkingSpotID, timeInterval);
//            Log.d("SEARCHRESERVE", "AFTERREQUEST");
//
//
//            NetworkPackage NP = controller.checkReceived();
//            MyEntry<String, Serializable> entry = NP.getCommand();
//            String key = entry.getKey();
//            Object value = entry.getValue();
//
//            if(key.equals("RESERVE")) {
//                HashMap<String, Serializable> map = (HashMap<String, Serializable>) value;
//                long reservationID = (long) map.get("RESERVATIONID");
//                String price = String.valueOf(map.get("PRICE"));
//                Log.d("price", price);
//
//                Intent intent = new Intent(getContext(), PaymentActivity.class);
//                intent.putExtra("RESERVATIONID", reservationID);
//                intent.putExtra("PROVIDERID", (Long)providerID);
//                intent.putExtra("PRICE", price);
//
//                startActivityForResult(intent, 11);
//                return true;
//            }
//            else if(key.equals("RESERVEFAIL")) {
//                Log.d("SEARCHRESERVE", "RESERVFAIL");
//                return false;
//            }
//            else {
//                Log.d("SEARCHRESERVE", "UNDEFINED ERROR");
//                return false;
//            }
//        }
//        @Override
//        protected void onPostExecute(Boolean success) {
//            if(success) {
//                progressDialog.dismiss();
//
//
//            } else{
//                progressDialog.dismiss();
//                Toast.makeText(getContext(), "Book space failed! Please try agian.", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }



    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewPager.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }




    private class RequestReviewTask extends AsyncTask<Void, Void, ArrayList<Review>> {
        long parkingSpotID;

        ArrayList<User> userlist;


        RequestReviewTask(long parkingSpotID) {
            this.parkingSpotID = parkingSpotID;
        }
        @Override
        protected ArrayList<Review> doInBackground(Void... params) {
            ClientController clientController = ClientController.getInstance();
            clientController.requestParkingSpotReview(parkingSpotID);
            NetworkPackage NP = clientController.checkReceived();
            MyEntry<String, Serializable> entry = NP.getCommand();
            String key = entry.getKey();
            Object value = entry.getValue();
            if (key.equals("REVIEWFORPARKINGSPOT")) {
                HashMap<String, Serializable> map = (HashMap<String, Serializable>)value;
                ArrayList<Review> list = (ArrayList<Review>) map.get("REVIEWS");
                Log.d("FETCHREVIEWLIST", "listsize: " + String.valueOf(list.size()));
                userlist = (ArrayList<User>) map.get("USERS");
                return list;
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Review> list) {
            if(list != null && userlist != null)
            {
                spotReviewStringList.clear();
                for(int i = 0; i < list.size(); i++)
                {
                    Log.d("FETCHREVIEW", list.get(i).comment);
                    spotReviewStringList.add(userlist.get(i).Fname + ":\n" + "Rating: " + String.valueOf(list.get(i).spotRating) + "\n" + "Comment: " + list.get(i).comment);
                }
                spotReivewListAdapter.notifyDataSetChanged();
                setListViewHeightBasedOnChildren(spotReviewList);
            }
        }
    }


    private class GetPublicProfileTask extends AsyncTask<Void, Void, HashMap<String, Serializable> >{
        private final long mProviderID;
        ProgressDialog progressDialog;

        GetPublicProfileTask(long providerID){
            mProviderID = providerID;
        }
        @Override
        protected void onPreExecute(){
            //Display a progress dialog
            progressDialog = new ProgressDialog(getActivity(),
                    R.style.AppTheme);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Searching...");
            progressDialog.show();
        }
        @Override
        protected HashMap<String, Serializable> doInBackground(Void... params ){
            ClientController clientController = ClientController.getInstance();
            clientController.fetchReviewsForUser(mProviderID);
            NetworkPackage NP = clientController.checkReceived();
            MyEntry<String, Serializable> entry = NP.getCommand();
            String key = entry.getKey();
            Object value = entry.getValue();
            if(key.equals("REVIEWSFORUSER")){
                return (HashMap<String, Serializable>)value;
            }
            return null;
        }

        @Override
        protected void onPostExecute(HashMap<String, Serializable> inPublicProfileInfo) {
            Context c = getContext();
            if(inPublicProfileInfo != null) {
                Log.d("Search Space Detail ", "GetPublicProfileTask inReview != null");
                progressDialog.dismiss();

                User user = (User) inPublicProfileInfo.get("USER");
                ArrayList<Review> list = (ArrayList<Review>)inPublicProfileInfo.get("REVIEWS");
                ArrayList<User> list2 = (ArrayList<User>)inPublicProfileInfo.get("USERS");
                Log.d("LISTSIZE", String.valueOf(list.size())+" "+String.valueOf(list2.size()));
                if(user != null && list != null) {
                    ArrayList<String> reviewsString = new ArrayList<String>();
                    for(int i = 0; i<list.size(); i++) {
                        String s = list2.get(i).userName+"-"+ list.get(i).spotRating + " - " + list.get(i).comment;
                        reviewsString.add(s);
                    }
                    PublicProfileFragment publicProfileFragment = new PublicProfileFragment();
                    Bundle args = new Bundle();
                    args.putString("FIRSTNAME", user.Fname);
                    args.putString("EMAIL", user.userName);
                    args.putString("PHONE_NUM", user.userPhone);
                    args.putStringArrayList("REVIEWS", reviewsString);
                    publicProfileFragment.setArguments(args);
                    //Activity ac = getActivity();
                    //if (ac instanceof RenterActivity)
                    ((RenterActivity) getActivity()).switchToPublicProfileFrag(publicProfileFragment);
                }
            } else{
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Get provider profile failed.", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
