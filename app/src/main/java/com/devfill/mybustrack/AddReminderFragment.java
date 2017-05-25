package com.devfill.mybustrack;


import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import biz.kasual.materialnumberpicker.MaterialNumberPicker;

public class AddReminderFragment extends Fragment implements SpinnerTextView.ISpinnerTextViewListener {

	EditText setDuration;
	String strSpin;
	String strEt;
	Button saveReminder,enterTime;

	private int routePos;
	private String duration;

	DBHelper dbHelper;

	SpinnerTextView spinnerTextView;

	public static final String LOG_TAG_DB = "AddReminderFragmentdbLogs";



	public interface IAddReminderFragment {
	    public void onClickSaveEvent();
	  }

	IAddReminderFragment iAddReminderFragment;

	public AddReminderFragment(IAddReminderFragment iAddReminderFragment){
		this.iAddReminderFragment = iAddReminderFragment;
	}

	
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
	Bundle savedInstanceState) {

	View rootView =  inflater.inflate(R.layout.add_reminder_fragment,container, false);
	//  View v = inflater.inflate(R.layout.camera_fragment, parent, false);
	spinnerTextView = (SpinnerTextView) rootView.findViewById(R.id.spinnertextview);
	spinnerTextView.setListener(this);

	saveReminder = (Button) rootView.findViewById(R.id.saveReminder);
	enterTime = (Button) rootView.findViewById(R.id.enterTime);



	dbHelper = new DBHelper(getContext());

	saveReminder.setOnClickListener(new OnClickListener() {
        public void onClick(View v) {
        	iAddReminderFragment.onClickSaveEvent();
			saveReminder(MainActivity.mEntries[routePos],duration);
        }
      });

	   enterTime.setOnClickListener(new OnClickListener() {
		  @Override
		  public void onClick(View v) {

			  NumberPicker picker = new NumberPicker(getContext());
			  picker.setMinValue(1);
			  picker.setMaxValue(10);

			  MaterialNumberPicker.Builder numberPickerBuilder = new MaterialNumberPicker.Builder(getContext());

			  numberPickerBuilder.minValue(1);
			  numberPickerBuilder.maxValue(10);
			  numberPickerBuilder.defaultValue(1);
			  numberPickerBuilder.backgroundColor(getContext().getResources().getColor(R.color.drawer_background));
			  numberPickerBuilder.separatorColor(getContext().getResources().getColor(R.color.colorAccent));
			  numberPickerBuilder.textColor(getContext().getResources().getColor(R.color.colorPrimary));
			  numberPickerBuilder.textSize(20);
			  numberPickerBuilder.enableFocusability(false);
			  numberPickerBuilder.wrapSelectorWheel(true);

			  enterTime.setText("10 минут");

			  numberPickerBuilder.formatter(new NumberPicker.Formatter() {
				  @Override
				  public String format(int value) {
					  return value + " минут";
				  }
			  });
			  picker = numberPickerBuilder.build();


			  final NumberPicker finalPicker = picker;

			   new AlertDialog.Builder(getActivity())
					  .setTitle("Время")
					  .setView(picker)
					  .setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
						  @Override
						  public void onClick(DialogInterface dialog, int which) {
							  duration = Integer.toString(finalPicker.getValue()) + " минут";
							  enterTime.setText(duration);
							  //  Toast.makeText(getContext(), "You picked : " + finalPicker.getValue(), Toast.LENGTH_LONG).show();
							  // Snackbar.make(findViewById(R.id.your_container), "You picked : " + numberPicker.getValue(), Snackbar.LENGTH_LONG).show();
						  }
					  })
					  .show();
		  }
	  });






    return rootView;
  }
	@Override
	public void onItemSelected(int position) {
		routePos = position;
	}
	public void saveReminder(String routeName,String duration){
		// подключаемся к БД
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		// делаем запрос всех данных из таблицы mytable, получаем Cursor
		Cursor c = db.query("mytable", null, null, null, null, null, null);
		ContentValues cv = new ContentValues();
		Log.d(LOG_TAG_DB, "--- Insert in mytable: ---");
		// подготовим данные для вставки в виде пар: наименование столбца - значение
		cv.put("routName", routeName);
		cv.put("duration", duration);


		//if (c.moveToFirst()){	//если таблица не пуста то обновляем знаения
			//int updCount = db.update("mytable", cv, "routName = ?",	//по наименованию маршрута
			//		new String[] { strSpin });
	//	}
		//else{	//если пуста
			// вставляем записьи и получаем ее ID
			long rowID = db.insert("mytable", null, cv);
			Log.d(LOG_TAG_DB, "row inserted, ID = " + rowID);
	//	}


		Log.d(LOG_TAG_DB, "--- Rows in mytable: ---");


		// ставим позицию курсора на первую строку выборки
		// если в выборке нет строк, вернется false
		if (c.moveToFirst()) {

			// определяем номера столбцов по имени в выборке
			int routNameColIndex = c.getColumnIndex("routName");
			int durationColIndex = c.getColumnIndex("duration");
			int durationRealColIndex = c.getColumnIndex("durationReal");
			int distanceColIndex = c.getColumnIndex("distance");
			int checkReminderStatusColIndex = c.getColumnIndex("checkReminderStatus");

			do {
				// получаем значения по номерам столбцов и пишем все в лог
				Log.d(LOG_TAG_DB,
						"routName = " + c.getString(routNameColIndex) +
								", duration = " + c.getString(durationColIndex) +
								", durationReal = " + c.getString(durationRealColIndex) +
								", distance = " + c.getString(distanceColIndex) +
								", checkReminderStatus = " + c.getInt(checkReminderStatusColIndex));
				// переход на следующую строку
				// а если следующей нет (текущая - последняя), то false - выходим из цикла
			} while (c.moveToNext());
		} else
			Log.d(LOG_TAG_DB, "0 rows");
			c.close();

		// Log.d(LOG_TAG_DB, "row inserted, ID = " + rowID);
		// закрываем подключение к БД
		dbHelper.close();
	}
  
}