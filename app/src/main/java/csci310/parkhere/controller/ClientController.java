package csci310.parkhere.controller;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import resource.CarType;
import resource.CustomImage;
import resource.MyEntry;
import resource.NetworkPackage;
import resource.ParkingSpot;
import resource.Reservation;
import resource.Review;
import resource.SearchResults;
import resource.Time;
import resource.TimeInterval;
import resource.User;


public class ClientController {

    private static final long serialVersionUID = 1239123098533917283L;

    private User user;
    public ArrayList<ParkingSpot> parkingSpots;
    public ArrayList<Reservation> renterReservations;
    public ArrayList<Reservation> providerReservations;
    public ArrayList<Review> reviews;
    public ClientCommunicator clientCommunicator;
    public String encodedProfilePic;

    private static ClientController instance;


    private static Activity currentActivity;
    private static Fragment currentFragment;

    public int currentIndexofSpaces;

    public LatLng currLocation;
    public SearchResults searchResults;
    // HashMap<Long, Integer> of <parking spot ID, amount of bookings>
    public HashMap<Long, Integer> searchResultsFreq;

    public boolean registerFailed;
    public boolean loginFailed;
    public boolean toDispaySearch;

    public boolean providerToshowSpaces;
    public boolean providerToshowSpacesDetail;
    //new
    private boolean received;
    public boolean receiving;
    private NetworkPackage NP;
    //

    private ClientController() throws IOException { // private constructor

        user = null;
        parkingSpots = new ArrayList<>();
        renterReservations = new ArrayList<>();
        providerReservations = new ArrayList<>();
        reviews = new ArrayList<>();
        clientCommunicator = new ClientCommunicator(this);
        encodedProfilePic = "";

        instance = this;

        registerFailed = false;
        loginFailed = false;
        toDispaySearch = false;
        providerToshowSpaces = false;
        providerToshowSpacesDetail = false;
        searchResults = null;
        searchResultsFreq = null;
        currentIndexofSpaces = -1;
        //new
        received = false;
        NP = null;
        //
    }

    public void setCurrentActivity(Activity ac)
    {
        currentActivity = ac;
    }
    public Activity getCurrentActivity(){
        return currentActivity;
    }
    public void setCurrentFragment(Fragment fr) { currentFragment = fr;}

    public static ClientController getInstance(){

        if(instance == null) {
            try {
                instance = new ClientController();
            } catch (IOException e) {
                e.printStackTrace();
                return null;

            }
        }
        return instance;
    }

    public void resetConnection()
    {
        if(currentActivity != null)
        {
            ResetConnAsync reset = new ResetConnAsync();
            reset.execute();
        }

    }


    public static void resetController()
    {
            instance = null;
    }

    // Getters
    public User getUser() { return user;}
    public ArrayList<ParkingSpot> getSpots() { return parkingSpots; }
    public ArrayList<Reservation> getRenterReservations() { return renterReservations; }
    public ArrayList<Review> getReviews() { return reviews; }

    // Setters
    public void setUser(User u) { user = u; }
    public void setSpots(ArrayList<ParkingSpot> spots) { parkingSpots = spots; }
    public void setRenterReservations(ArrayList<Reservation> res) { renterReservations = res; }
    public void setReviews(ArrayList<Review> rev) { reviews = rev; }

    // TODO: Functions for login, signup
    public void login(String username, String pw) throws IOException {

        Log.d("LOGIN", "Try to Login");
        HashMap<String, Serializable> entry = new HashMap<>();
        entry.put("USERNAME", username);
        entry.put("PASSWORD", pw);
        clientCommunicator.send("LOGIN", entry);
    }

    public void verifyRegister(String username) throws IOException {
        NetworkPackage NP = new NetworkPackage();
        NP.addEntry("VERIFICATION", username);
//        try {
            clientCommunicator.sendPackage(NP);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public void register(String username, String pw, String phone, String license, String plate, String usertype, String name) throws IOException {
        HashMap<String, Serializable> entry = new HashMap<>();
        entry.put("USERNAME", username);
        entry.put("PASSWORD", pw);
        entry.put("NAME", name);
        entry.put("PHONE", Long.parseLong(phone));
        entry.put("LICENSE", license);
        entry.put("PLATE", plate);
        boolean usertype_bool;
        if(usertype=="renter"){
            usertype_bool = true;
        } else{
            usertype_bool = false;
        }
        entry.put("USERTYPE", usertype_bool);
        clientCommunicator.send("REGISTER", entry);
    }

    public void forgotPW(String username) {
        NetworkPackage NP = new NetworkPackage();
        NP.addEntry("UPDATEPASSWORD", username);
//        try {
            clientCommunicator.sendPackage(NP);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    // Get unconfirmed reservations by user
    public void getConfirmListWithUserID(long userID) {
        NetworkPackage NP = new NetworkPackage();
        NP.addEntry("FETCHUNPAIDRESERVATION", userID);
//        try {
            clientCommunicator.sendPackage(NP);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public void updateReceived(NetworkPackage NP){
        this.NP = NP;
        received = true;
    }

    public void cancelReceived(){
        received = false;
        NP = null;
    }

    public NetworkPackage checkReceived(){
//        if(receiving) {

            while (received == false || NP == null) {
                if(!clientCommunicator.connecting)
                {
                    Log.d("Not connecting", "gg");
                    NetworkPackage np = new NetworkPackage();
                    np.addEntry("BADNETWORK", "BADNETWORK");
                    return np;
                }
            }
            received = false;
            if (NP == null)
                Log.d("CHECKRECEIVE", "NULL");

            return NP;
//        }
//        else{
//            return null;
//        }
    }


    // TODO: Functions for renter
    public void ProviderCancel(long timeIntervalID) {
        NetworkPackage NP = new NetworkPackage();
        NP.addEntry("PROVIDERCANCEL", timeIntervalID);
//        try {
            clientCommunicator.sendPackage(NP);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public void RenterCancel(long resID) {
        NetworkPackage NP = new NetworkPackage();
        NP.addEntry("RENTERCANCEL", resID);
//        try {
            clientCommunicator.sendPackage(NP);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

//    public Reservation getReservationDetail(int position) {
//        return renterReservations.get(position);
//    }

    public void submitReview(long reservationID, int rating, String comment) {
        HashMap<String, Serializable> map = new HashMap<>();
        map.put("RESERVATIONID", reservationID);
        map.put("REVIEWDESCRIPTION", comment);
        map.put("RATING", rating);
//        try {
            clientCommunicator.send("REVIEW", map);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
//
    public void report(long reservationID) {
//        try {
            clientCommunicator.send("REPORT", reservationID);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public void search(LatLng location, String startDate, String startTime, String endDate, String endTime, String carType, String distance) throws IOException {
        currLocation = location;
        String[] time1 = startDate.split("-");
        String[] time11 = startTime.split("-");
        String[] time2 = endDate.split("-");
        String[] time22 = endTime.split("-");
        Log.d("time", time1[0] + " " + time1[1] + " "+time1[2]+ " "+ time11[0]+" "+time11[1]+ " "+time2[0] + " " + time2[1] + " "+time2[2]+ " "+ time22[0]+" "+time22[1]+ " " );
        Time inStartTime = new Time(Integer.parseInt(time1[2]),Integer.parseInt(time1[0]), Integer.parseInt(time1[1]), Integer.parseInt(time11[0]), Integer.parseInt(time11[1]),0);
        Time inEndTime = new Time(Integer.parseInt(time2[2]),Integer.parseInt(time2[0]), Integer.parseInt(time2[1]), Integer.parseInt(time22[0]), Integer.parseInt(time22[1]),0);
//        inStartTime.month -=;
//        inEndTime.month -=1;


        TimeInterval timeInterval = new TimeInterval(inStartTime, inEndTime);
        HashMap<String, Serializable> entry = new HashMap<>();
        if(location == null)
        {
            Toast.makeText(currentActivity.getApplicationContext(), "Please input search address", Toast.LENGTH_SHORT).show();

            return;
        }


        ParkingSpot.Location current_location = new ParkingSpot.Location(location.longitude, location.latitude);
        entry.put("LOCATION", current_location);
        entry.put("TIMEINTERVAL", timeInterval);
        entry.put("CARTYPE", carType);
        entry.put("DISTANCE", Integer.parseInt(distance.replaceAll("[\\D]", "")));
        clientCommunicator.send("SEARCH", entry);
    }

    public SearchResults sortSearchResultByPrice() {
        if(searchResults != null) {
            // ArrayList<ParkingSpot> searchResultList
            Collections.sort(searchResults.searchResultList, new Comparator<ParkingSpot>() {
                public int compare(ParkingSpot p1, ParkingSpot p2) {
                    return Double.compare(p1.search_price, p2.search_price);
                }
            });
        }
        return searchResults;
    }

    public SearchResults sortSearchResultByDist() {
        if(searchResults != null) {
            Collections.sort(searchResults.searchResultList, new Comparator<ParkingSpot>() {
                public int compare(ParkingSpot p1, ParkingSpot p2) {
                    double p1LatDiff = p1.getLat()-currLocation.latitude;
                    double p1LongDiff = p1.getLon()-currLocation.longitude;
                    double p1Dist = Math.sqrt(p1LatDiff*p1LatDiff + p1LongDiff*p1LongDiff);

                    double p2LatDiff = p2.getLat()-currLocation.latitude;
                    double p2LongDiff = p2.getLon()-currLocation.longitude;
                    double p2Dist = Math.sqrt(p2LatDiff*p2LatDiff + p2LongDiff*p2LongDiff);

                    return Double.compare(p1Dist, p2Dist);
                }
            });
        }
        return searchResults;
    }

    public SearchResults sortSearchResultBySpotRating() {
        if(searchResults != null) {
            Collections.sort(searchResults.searchResultList, new Comparator<ParkingSpot>() {
                public int compare(ParkingSpot p1, ParkingSpot p2) {
                    return Double.compare(p1.review, p2.review);
                }
            });
        }
        return searchResults;
    }

    public SearchResults sortSearchResultByProviderRating() {
        if(searchResults != null) {
            Collections.sort(searchResults.searchResultList, new Comparator<ParkingSpot>() {
                public int compare(ParkingSpot p1, ParkingSpot p2) {
                    return Double.compare(p1.providerReview, p2.providerReview);
                }
            });
        }
        return searchResults;
    }

    public void addSpace(LatLng location, String streetAddress, String description, String carType, int cancelPolicy)
    {
        Log.d("@@@Controller", " addSpace called!");
        if(location == null)
            return;

        int mCarType = new CarType(carType).findNum();
        ParkingSpot spot = new ParkingSpot(user.userID,null,location.latitude,location.longitude,streetAddress,description, "",mCarType,cancelPolicy);
        //public ParkingSpot(long userID, ArrayList<TimeInterval> time, double lat, double lon, String streetAddr, String description, String zipcode, int cartype) {
//        try {
            Log.d("@@@Controller", " ADD_PARKINGSPOT");
            clientCommunicator.send("ADD_PARKINGSPOT", spot);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public void requestPaymentToken() {
        NetworkPackage NP = new NetworkPackage();
        NP.addEntry("TOKENREQUEST", null);
//        try {
            clientCommunicator.sendPackage(NP);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public void postPaymentNonceToServer(String paymentMethodNonce, long resID, long providerID, String price, String paymentMethodType)
    {
        HashMap<String, Serializable> map = new HashMap<>();
        map.put("PAYMENTNONCE", paymentMethodNonce);
        map.put("RESERVATIONID", resID);
        map.put("PROVIDERID", providerID);
        map.put("PRICE", price);
        map.put("TYPE", paymentMethodType);

            clientCommunicator.send("PAYMENT_SUCCESS", map);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public void submitPaymentRequest(long resID) {
        NetworkPackage NP = new NetworkPackage();
        NP.addEntry("CONFIRMPAYMENT", resID);
//        try {
            clientCommunicator.sendPackage(NP);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }


    public void requestMyProviderReservationList()
    {
        if(user == null)
        {
            return;
        }

        NetworkPackage NP = new NetworkPackage();
        NP.addEntry("FETCHRESERVATIONBYPROVIDERID", user.userID);

//        try {
            clientCommunicator.sendPackage(NP);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public void requestMyRenterReservationList()
    {
        if(user == null)
            return;

        NetworkPackage NP = new NetworkPackage();
        NP.addEntry("FETCHRESERVATION", user.userID);
//        try {
            clientCommunicator.sendPackage(NP);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public void requestMyParkingSpotList()
    {
        if(user == null)
            return;

        NetworkPackage NP = new NetworkPackage();
        NP.addEntry("FETCHPARKINGSPOT", user.userID);
//        try {
            clientCommunicator.sendPackage(NP);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public void requestSpotTimeInterval(ParkingSpot spot)
    {
        if(user == null && spot == null)
        {
            return;
        }

        NetworkPackage NP = new NetworkPackage();
        NP.addEntry("FETCHTIMEINTERVAL", spot.getParkingSpotID());
//        try {
            clientCommunicator.sendPackage(NP);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public ArrayList<TimeInterval> requestSpotTimeIntervalWithDate(long spotID, String date) {
        ArrayList<TimeInterval> list = new ArrayList<TimeInterval>();
        Time TestStart = new Time(date+" 0:0:0");
        Time TestEnd = new Time(date+" 23:59:59");
        Log.d("TEST1", TestStart.toString());
        Log.d("TEST2", TestEnd.toString());
        for(int i = 0; i < parkingSpots.size(); i++) {
            ParkingSpot spot = parkingSpots.get(i);
            if(spot.getParkingSpotID() == spotID) {
                ArrayList<TimeInterval> list2 = spot.getTimeIntervalList();
                Log.d("TEST3", String.valueOf(list2.size()));
                for(int k = 0; k <list2.size(); k++){
                    String startTime1 = list2.get(k).startTime.toString();
                    String[] startTime11 = startTime1.split(" ");
                    String startTime111 = startTime11[0]+" 0:0:0";
                    Time start = new Time(startTime111);
                    String endTime1 = list2.get(k).endTime.toString();
                    Log.d("TEST4", startTime1+" "+endTime1);
                    String[] endTime11 = endTime1.split(" ");
                    String endTime111 = endTime11[0]+" 23:59:59";
                    Time end = new Time(endTime111);
                    Log.d("TEST5", end.toString()+" "+start.toString()+" "+TestStart.toString()+" "+TestEnd.toString());
                    if(start.compareTo(TestStart)<=0){
                        if(end.compareTo(TestEnd)>=0){
                            list.add(list2.get(k));
                        }
                    }
                }
            }
        }
        return list;
    }

    public void setSpotTimeInterval(long spotID, ArrayList<TimeInterval> intervals) {
        for(int i = 0; i < parkingSpots.size(); i++) {
            ParkingSpot spot = parkingSpots.get(i);
            if(spot.getParkingSpotID() == spotID)
            {
                spot.setTimeIntervalList(intervals);
            }
        }
    }

    //the EditTime for provider
    public void requestEditTime(long timeSlotID, Time startTime, Time endTime, double price){
        TimeInterval timeInterval = new TimeInterval(startTime, endTime);
        NetworkPackage NP = new NetworkPackage();
        HashMap<String, Serializable> map = new HashMap<>();
        map.put("TIMESLOTID", timeSlotID);
        map.put("TIMEINTERVAL", timeInterval);
        System.out.println("Start:"+timeInterval.startTime);
        System.out.println("End:" + timeInterval.endTime);
        map.put("PRICE", price);
        NP.addEntry("EDITTIME", map);
//        try {
            clientCommunicator.sendPackage(NP);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public void requestParkingSpotReview(long parkingSpotid)
    {
        NetworkPackage NP = new NetworkPackage();
        NP.addEntry("FETCHREVIEWSFORPARKINGSPOT", parkingSpotid);
//        try {
            clientCommunicator.sendPackage(NP);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public void requestAddTime(ParkingSpot spot, Time startTime, Time endTime, double price)
    {
        TimeInterval timeInterval = new TimeInterval(startTime, endTime);
        NetworkPackage NP = new NetworkPackage();
        HashMap<String, Serializable> map = new HashMap<>();
        map.put("PARKINGSPOTID", spot.getParkingSpotID());
        map.put("TIMEINTERVAL", timeInterval);
        map.put("PRICE", price);
        System.out.println("Start:"+timeInterval.startTime);
        System.out.println("End:" + timeInterval.endTime);
        map.put("PRICE", price);
        NP.addEntry("ADDTIME", map);
//        try {
            clientCommunicator.sendPackage(NP);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public void deleteSpace(long parkingSpotid){
        NP.addEntry("DELETESPACE", parkingSpotid);
//        try {
            clientCommunicator.sendPackage(NP);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
    public void logout(boolean userType) {
        NetworkPackage NP = new NetworkPackage();
        HashMap<String, Serializable> map = new HashMap<>();
        map.put("USERID", user.userID);
        map.put("USERTYPE", userType);
        NP.addEntry("LOGOUT", map);
//        try {
            clientCommunicator.sendPackage(NP);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public void renterReserve(long userID, long parkingSpotID, TimeInterval timeInterval){
        NetworkPackage NP = new NetworkPackage();
        HashMap<String, Serializable> map = new HashMap<String, Serializable>();
        map.put("PARKINGSPOTID", parkingSpotID);
        map.put("RENTERID", userID);
        map.put("TIMEINTERVAL", timeInterval);
        NP.addEntry("RESERVE", map);
//        try {
            clientCommunicator.sendPackage(NP);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public void editProfile(String username, String password, String userLicense, String userPlate, String phone){
        NetworkPackage NP = new NetworkPackage();
        user.userName = username;
        user.userLicense = userLicense;
        user.userPlate = userPlate;
        user.userPhone = phone;
        HashMap<String, Serializable> map = new HashMap<String, Serializable>();
        map.put("USER", user);
        map.put("PASSWORD", password);
        NP.addEntry("EDITPROFILE", map);
//        try {
            clientCommunicator.sendPackage(NP);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public void sendImagetoServer(String DataURI, String Identifier, long ID)
    {
        CustomImage customImage = new CustomImage();
        customImage.Data_URI = DataURI;
        if(Identifier.equals("PARKINGSPACEIMAGE"))
        {
            customImage.parkingSpotID = ID;
            customImage.UserID = -1;
            NP.addEntry("UPLOADIMAGE",customImage);

        }
        else if(Identifier.equals("USERPROFILEIMAGE"))
        {
            customImage.parkingSpotID = -1;
            customImage.UserID = ID;
            NP.addEntry("UPLOADPROFILEIMAGE", customImage);
        }
        else
        {
            Log.d("WRONG", "WRONG IDENTIFIER");
        }
//        try {
            clientCommunicator.sendPackage(NP);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public void fetchReviewsForUser(long providerID){
        NP.addEntry("FETCHREVIEWSFORUSER", providerID);
//        try {
            clientCommunicator.sendPackage(NP);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public void fetchReviewsForParkingSpot(long parkingSpotID){
        NP.addEntry("FETCHREVIEWSFORPARKINGSPOT", parkingSpotID);
//        try{
            clientCommunicator.sendPackage(NP);
//        } catch(IOException e){
//            e.printStackTrace();
//        }
    }



    public void fetchThumbNailImg(ArrayList<Long> listParkingSpotID)
    {
        NP.addEntry("GETTHUMBNAILIMG", listParkingSpotID);
//        try{
            clientCommunicator.sendPackage(NP);
//        } catch(IOException e){
//            e.printStackTrace();
//        }
    }

    public void sendImagesToServer(ArrayList<String> images, String Identifier, long ID){
        NetworkPackage NP1;
        for(int i = 0 ; i< images.size();i++){
            sendImagetoServer(images.get(i), Identifier, ID);
            NP1 = checkReceived();
            MyEntry<String, Serializable> entry = NP.getCommand();
            String key = entry.getKey();
            while (!key.equals("STOREIMAGESUCCESS")) {
                Log.d("dame:", "zhende");
                sendImagetoServer(images.get(i), Identifier, ID);
            }
        }
    }

    public void getParkingSpotImages(String identifier, long id) {
        if(identifier.equals("PARKINGSPOT")) {
            NP.addEntry("GETSPOTIMAGES", id);
        } else{
            NP.addEntry("GETUSERIMAGES", id);
        }

//        try{
            clientCommunicator.sendPackage(NP);
//        } catch(IOException e){
//            e.printStackTrace();
//        }
    }

    //the editParkingSpot which will be called by the provider
    public void editParkingSpot(ParkingSpot ps) {
        //map.put("PICTURE", imageStrings);
        Log.d("ps", "send ps");
        NP.addEntry("EDITSPACE", ps);
//        try {
            clientCommunicator.sendPackage(NP);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public void deleteOldParkingSpotImages(ParkingSpot ps)
    {

        NetworkPackage updateImageNP = new NetworkPackage();
        updateImageNP.addEntry("DELETEOLDPARKINGSPOTIMAGES", ps.getParkingSpotID());

//        try {
            clientCommunicator.sendPackage(updateImageNP);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public void getUserWithID(long userID) {
        NP.addEntry("GETUSERWITHID", userID);
//        try {
            clientCommunicator.sendPackage(NP);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }


    public void providerReport(long resID){
        NP.addEntry("REPORT", resID);
//        try {
            clientCommunicator.sendPackage(NP);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public void getPrice(long userID, long parkingSpotID, TimeInterval timeInterval) {
        NetworkPackage NP = new NetworkPackage();
        HashMap<String, Serializable> map = new HashMap<String, Serializable>();
        map.put("PARKINGSPOTID", parkingSpotID);
        map.put("RENTERID", userID);
        map.put("TIMEINTERVAL", timeInterval);
        NP.addEntry("PRICE", map);
//        try {
            clientCommunicator.sendPackage(NP);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }


//    public void stopReceiving() {
//        receiving = false;
//    }
//
//    public void startReceiving(){
//        receiving = true;
//    }


    private class ResetConnAsync extends AsyncTask<Void, Void, Void> {



        ResetConnAsync() {


        }

        @Override
        protected Void doInBackground(Void... params) {

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(currentActivity.getApplicationContext(), "Reconnecting", Toast.LENGTH_LONG).show();
            clientCommunicator = new ClientCommunicator(ClientController.this);

        }
    }

    public void getProfilePic(long userID) {
        NetworkPackage NP = new NetworkPackage();
        NP.addEntry("GETUSERIMAGE", userID);
        clientCommunicator.sendPackage(NP);

    }
}