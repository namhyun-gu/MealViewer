package com.earlier.yma.util;

/**
 * Created by namhyun on 2016-03-07.
 */
public class IabConfig {
    public static String SKU_REMOVE_AD = "remove_ad";
//    private final String BIND_SERVICE_NAME = "com.android.vending.billing.InAppBillingService.BIND";
//    private final int BILLING_API_VERSION = 3;
//
//    private final int BILLING_RESPONSE_RESULT_OK = 0;
//    private final int BILLING_RESPONSE_RESULT_USER_CANCELED = 1;
//    private final int BILLING_RESPONSE_RESULT_SERVICE_UNAVAILABLE = 2;
//    private final int BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE = 3;
//    private final int BILLING_RESPONSE_RESULT_ITEM_UNAVAILABLE = 4;
//    private final int BILLING_RESPONSE_RESULT_DEVELOPER_ERROR = 5;
//    private final int BILLING_RESPONSE_RESULT_ERROR = 6;
//    private final int BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED = 7;
//    private final int BILLING_RESPONSE_RESULT_ITEM_NOT_OWNED = 8;
//
//    private IInAppBillingService mService;
//    private ServiceConnection mServiceConnection;
//    private Activity mActivity;
//    private ArrayList<String> mItemList;
//
//    public IabConfig(Activity mActivity) {
//        this.mActivity = mActivity;
//        this.initalize();
//        this.initalizeItemList();
//    }
//
//    private void initalizeItemList() {
//        mItemList = new ArrayList<>();
//        mItemList.add("remove_ad");
//    }
//
//    private void initalize() {
//        mServiceConnection = new ServiceConnection() {
//            @Override
//            public void onServiceConnected(ComponentName name, IBinder service) {
//                mService = IInAppBillingService.Stub.asInterface(service);
//            }
//
//            @Override
//            public void onServiceDisconnected(ComponentName name) {
//                mService = null;
//            }
//        };
//    }
//
//    public IInAppBillingService getService() {
//        return mService;
//    }
//
//    public void bind() {
//        mActivity.bindService(new Intent(BIND_SERVICE_NAME), mServiceConnection, Context.BIND_AUTO_CREATE);
//    }
//
//    public void unbind() {
//        if (mService != null)
//            mActivity.unbindService(mServiceConnection);
//    }
//
//    public ArrayList<SkuDetails> requestItemList() {
//        Bundle querySkus = new Bundle();
//        querySkus.putStringArrayList("ITEM_ID_LIST", mItemList);
//
//        ArrayList<SkuDetails> skuDetailsArrayList = new ArrayList<>();
//        try {
//            Bundle skuDetails =
//                    mService.getSkuDetails(BILLING_API_VERSION, mActivity.getPackageName(), "inapp", querySkus);
//
//            int response = skuDetails.getInt("RESPONSE_CODE");
//            if (response == BILLING_RESPONSE_RESULT_OK) {
//                ArrayList<String> responseList = skuDetails.getStringArrayList("DETAILS_LIST");
//                for (String responseString : responseList) {
//                    Gson gson = new Gson();
//                    SkuDetails details = gson.fromJson(responseString, SkuDetails.class);
//                    skuDetailsArrayList.add(details);
//                }
//            }
//        } catch (RemoteException e) {
//            e.printStackTrace();
//            return null;
//        }
//        return skuDetailsArrayList;
//    }
}
