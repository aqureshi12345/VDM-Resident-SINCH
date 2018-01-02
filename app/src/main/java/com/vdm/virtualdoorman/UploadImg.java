package com.vdm.virtualdoorman;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONObject;

import com.vdm.virtualdoorman.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class UploadImg extends Activity {
	String filePath = null;
	private static final int PICK_IMAGE = 1;
	Button btnTakePhoto;
	Button Upload;
	static final int REQUEST_IMAGE_CAPTURE = 2;
	ImageView icon;
	String mCurrentPhotoPath;
	HttpResponse response = null;
	String result;
	private ProgressDialog pDialog;
	private Bitmap bitmap;
	MenuItem item;
	File imgFile;
	Intent intent;
	JSONObject jsonobj = new JSONObject();
	JSONParser jsonParser = new JSONParser();
	SharedPreferences loginpref;

	public static final String LOGINPREF = "LoginPrefs";
	String login_guid = null;

	private static String url_image_upload = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.upload_photo);
		intent = getIntent();
		// Buttons
		btnTakePhoto = (Button) findViewById(R.id.btnTakePhoto);
		Upload = (Button) findViewById(R.id.btnUpload);
		icon = (ImageView) findViewById(R.id.icon);
		loginpref = getSharedPreferences(LOGINPREF, Context.MODE_PRIVATE);

		login_guid = (loginpref.getString("login_guid", ""));
		url_image_upload = "https://portal.virtualdoorman.com/dev/iphone/iphone_requests.php?function=update_image&login_guid="
				+ login_guid;
		// view products click event
		btnTakePhoto.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				// Launching All products Activity

				dispatchTakePictureIntent();

			}
		});

		Upload.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {

				try {
					Intent intent = new Intent(
							Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					// intent.setType("image/*");
					// intent.setAction(Intent.ACTION_GET_CONTENT);
					startActivityForResult(intent, PICK_IMAGE);
				} catch (Exception e) {
					Toast.makeText(getApplicationContext(), "error",
							Toast.LENGTH_LONG).show();
					Log.e(e.getClass().getName(), e.getMessage(), e);
				}

			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case PICK_IMAGE:
			if (resultCode == Activity.RESULT_OK) {
				Uri selectedImageUri = data.getData();
				// String filePath = null;

				try {
					// OI FILE Manager
					String filemanagerstring = selectedImageUri.getPath();

					// MEDIA GALLERY
					String selectedImagePath = getPath(selectedImageUri,
							"pickImage");

					if (selectedImagePath != null) {
						filePath = selectedImagePath;
					} else if (filemanagerstring != null) {
						filePath = filemanagerstring;
					} else {
						Toast.makeText(getApplicationContext(), "Unknown path",
								Toast.LENGTH_LONG).show();
						Log.e("Bitmap", "Unknown path");
					}

					if (filePath != null) {
						imgFile = new File(filePath);
						decodeFile(filePath);
						new ImageUploadTask().execute();
					} else {
						bitmap = null;
					}
				} catch (Exception e) {
					Toast.makeText(getApplicationContext(), "Internal error",
							Toast.LENGTH_LONG).show();
					Log.e(e.getClass().getName(), e.getMessage(), e);
				}
			}
			break;
		case REQUEST_IMAGE_CAPTURE:
			if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
				Bundle extras = data.getExtras();
				Bitmap imageBitmap = (Bitmap) extras.get("data");
				Uri imageUri = getImageUri(getApplicationContext(), imageBitmap);
				imgFile = new File(getPath(imageUri, "camera"));
				decodeFile(getPath(imageUri, "camera"));
				/*int height = icon.getHeight();
				int width = icon.getWidth();
				Bitmap ScaledBitmap = Bitmap.createScaledBitmap(imageBitmap,
						width, height, false);
				icon.setImageBitmap(ScaledBitmap);*/
				new ImageUploadTask().execute();
			
			}

			break;
		default:
		}
		
	}

	public void decodeFile(String filePath) {
		// Decode image size
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, o);
		BitmapFactory.Options o2 = new BitmapFactory.Options();
		bitmap = BitmapFactory.decodeFile(filePath, o2);

		int height = icon.getHeight();
		int width = icon.getWidth();
		Bitmap ScaledBitmap = Bitmap.createScaledBitmap(bitmap, width, height,
				false);
		icon.setImageBitmap(ScaledBitmap);
	}

	public String getPath(Uri uri, String state) {
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = null;
		if (state == "camera")
			cursor = getContentResolver().query(uri, null, null, null, null);
		if (state == "pickImage")
			cursor = getContentResolver().query(uri, projection, null, null,
					null);
		// managedQuery(uri, projection, null, null, null);
		if (cursor != null) {
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} else
			return null;
	}

	class ImageUploadTask extends AsyncTask<Void, Void, String> {
		String sResponse = null;

		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(UploadImg.this);
			pDialog.setMessage("Uploading Photo..");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(Void... unsued) {
			// String sResponse = null;
			try {
				HttpClient httpClient = new DefaultHttpClient();
				HttpContext localContext = new BasicHttpContext();

				HttpPost httppost = new HttpPost(url_image_upload);
				MultipartEntity entity = new MultipartEntity();

				entity.addPart("person_id", intent.getStringExtra("id"));
				entity.addPart("files", imgFile);
				httppost.setEntity(entity);
				HttpResponse response = httpClient.execute(httppost,
						localContext);
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(
								response.getEntity().getContent(), "UTF-8"));

				sResponse = reader.readLine();

			} catch (Exception e) {
				e.printStackTrace();

			}
			return sResponse;

		}

		@Override
		protected void onProgressUpdate(Void... unsued) {

		}

		@Override
		protected void onPostExecute(String sResponse) {
			pDialog.dismiss();
			try {
				JSONObject JResponse = new JSONObject(sResponse);
				String response = JResponse.getString("response");
				if (response
						.equalsIgnoreCase("Image has been uploaded successfully")) {
					Toast.makeText(getApplicationContext(), response,
							Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(getApplicationContext(),
							"Some Error Occured", Toast.LENGTH_SHORT).show();

				}
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(), "Error at server side",
						Toast.LENGTH_LONG).show();
				Log.e(e.getClass().getName(), e.getMessage(), e);
			}
		}
	}

	private void dispatchTakePictureIntent() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

			File photoFile = null;
			try {
				photoFile = createImageFile();

			} catch (IOException ex) {
				ex.printStackTrace();

			}

			// Continue only if the File was successfully created
			if (photoFile != null) {
				imgFile = new File(photoFile.getAbsolutePath());
				// takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
				// Uri.fromFile(photoFile));
				startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

			}

		}
	}

	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		File storageDir = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		File image = File.createTempFile(imageFileName, /* prefix */
				".jpg", /* suffix */
				storageDir /* directory */
		);

		// Save a file: path for use with ACTION_VIEW intents
		mCurrentPhotoPath = "file:" + image.getAbsolutePath();
		return image;
	}

	public Uri getImageUri(Context inContext, Bitmap inImage) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
		String path = Images.Media.insertImage(inContext.getContentResolver(),
				inImage, "Title", null);
		return Uri.parse(path);
	}

}
