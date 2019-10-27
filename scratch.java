import java.util.List;
import java.util.ArrayList;

protected static Place getCurrentPlace()
{
	// Use fields to define the data types to return.
	Place.Field[] fields = {Place.Field.Place.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.TYPES};
	List<Place.Field> placeFields = Arrays.asList(fields);

	// Use the builder to create a FindCurrentPlaceRequest.
	FindCurrentPlaceRequest request =
			FindCurrentPlaceRequest.newInstance(placeFields);

	double bestLikelihood = -1.0;
	Place mostLikely;

	// Call findCurrentPlace and handle the response (first check that the user has granted permission).
	if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
	{
		Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);
		placeResponse.addOnCompleteListener(task -> {
			if (task.isSuccessful())
			{
				FindCurrentPlaceResponse response = task.getResult();
				for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods())
				{
					Log.i(TAG, String.format("Place '%s' has likelihood: %f",
							placeLikelihood.getPlace().getName(),
							placeLikelihood.getLikelihood()));

					if (placeLikelihood.getLikelihood() > bestLikelihood)
					{
						bestLikelihood = placeLikelihood.getLikelihood();
						mostLikely = placeLikelihood.getPlace();
					}
				}
			}
			else
			{
				Exception exception = task.getException();
				if (exception instanceof ApiException)
				{
					ApiException apiException = (ApiException) exception;
					Log.e(TAG, "Place not found: " + apiException.getStatusCode());
				}
			}
		});
	}
	else
	{
		// A local method to request required permissions;
		// See https://developer.android.com/training/permissions/requesting
		getLocationPermission();
	}
}

protected static boolean getLocationPermission()
{
	// Here, thisActivity is the current activity
	if (ContextCompat.checkSelfPermission(thisActivity, Manifest.permission.ACCESS_FINE_LOCATION)
			!= PackageManager.PERMISSION_GRANTED)
	{
		// Permission is not granted
		// Should we show an explanation?
		if (ActivityCompat.shouldShowRequestPermissionRationale(thisActivity,
				Manifest.permission.ACCESS_FINE_LOCATION)) {
			// Show an explanation to the user *asynchronously* -- don't block
			// this thread waiting for the user's response! After the user
			// sees the explanation, try again to request the permission.
		} else {
			// No explanation needed; request the permission
			ActivityCompat.requestPermissions(thisActivity,
					new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
					MY_PERMISSIONS_REQUEST_READ_CONTACTS);

			// MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
			// app-defined int constant. The callback method gets the
			// result of the request.
		}
	} else {
		// Permission has already been granted
	}

}

@Override
public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
{
    switch (requestCode)
	{
        case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted, yay! Do the
                // contacts-related task you need to do.
            } else {
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
            }
            return;
        }

        // other 'case' lines to check for other
        // permissions this app might request.
    }
}


