package com.tobacco.pos.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.tobacco.R;
import com.tobacco.pos.entity.ConsumeModel;
import com.tobacco.pos.entity.AllTables.Consume;
import com.tobacco.pos.entity.AllTables.GoodsPrice;
import com.tobacco.pos.handler.ConsumeHandler;

public class ConsumeInsert extends Activity{
	
	private static final String TAG = "ConsumeInsert";
	
	private static final int MENU_INPUT_CONSUME = Menu.FIRST;
	private static final int MENU_CONFIRM = Menu.FIRST+1;
	private static final int MENU_CANCEL = Menu.FIRST+2;
	private static final int MENU_REMOVE = Menu.FIRST+3;
	
	private static final int SAVE_STATE = 1;
	private static final int UNSAVE_STATE = 0;
	private int state;
	private static final int GET_CON = 0;
	
	private ConsumeHandler handler = new ConsumeHandler(this);
	private final ArrayList<ConsumeModel> consumeGoods = new ArrayList<ConsumeModel>();
	private final HashMap<TableRow,ConsumeModel> mapping = new HashMap<TableRow,ConsumeModel>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		state = SAVE_STATE;
		Intent intent = getIntent();
		if(intent.getData()==null)
			intent.setData(Consume.CONTENT_URI);
		this.setContentView(R.layout.consume_insert);
		
//		IntentFilter filter = new IntentFilter("com.tobacco.action.scan");
//		this.registerReceiver(new ScanReceiver(), filter);
//		this.startService(new Intent(this,ScanInputService.class));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0, MENU_INPUT_CONSUME, 0, "手动输入")
		.setIcon(android.R.drawable.ic_menu_add);
		
		menu.add(0, MENU_CANCEL, 0, "退出")
		.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
		
		menu.add(0, MENU_CONFIRM, 0, "保存")
		.setIcon(android.R.drawable.ic_menu_save);
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()){
		case MENU_INPUT_CONSUME:
			Intent intent = new Intent("com.tobacco.pos.activity.ConsumeInsertDialog");
			this.startActivityForResult(intent, GET_CON);
			return true;
		case MENU_CANCEL:
			if(state == UNSAVE_STATE){
				new AlertDialog.Builder(ConsumeInsert.this)
	            .setIcon(R.drawable.alert_dialog_icon)
	            .setTitle("尚未保存,确定要退出吗？")
	            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton) {
	                	finish();
	                }
	            })
	            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton) {
	                	
	                }
	            })
	            .show();
			}
			else{
				finish();
			}
			return true;
		case MENU_CONFIRM:
//			for(ConsumeModel goods : consumeGoods)
//				handler.insert(goods);
//			
//			Toast.makeText(ConsumeInsert.this, "保存成功", Toast.LENGTH_SHORT).show();
//			state = SAVE_STATE;
//			TableLayout table = (TableLayout)findViewById(R.id.consumeInsertTable);
//			table.removeViews(1, table.getChildCount()-1);
//			onResume();
			save();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	protected void save(){
		for(ConsumeModel goods : consumeGoods)
			handler.insert(goods);
		
		Toast.makeText(ConsumeInsert.this, "保存成功", Toast.LENGTH_SHORT).show();
		state = SAVE_STATE;
		TableLayout table = (TableLayout)findViewById(R.id.consumeInsertTable);
		table.removeViews(1, table.getChildCount()-1);
		onResume();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(requestCode == GET_CON)
		{
			if(resultCode == RESULT_OK){
				String barcode = data.getExtras().getString(GoodsPrice.barcode);
				int count = Integer.valueOf(data.getExtras().getString(Consume.NUMBER));
				String comment = data.getExtras().getString(Consume.COMMENT);
				
				ConsumeModel goods = handler.fillVacancy(barcode, count, comment);
				
				addConsumeGoods(goods);
//				showConsumeGoods(barcode,count,comment);
				state = UNSAVE_STATE;
			}
		}
	}
	
	private void addConsumeGoods(ConsumeModel goods){
		boolean exits = false;
		for(TableRow row : mapping.keySet()){
			ConsumeModel preGoods = mapping.get(row);
			if(preGoods.getGoodsPriceId()==goods.getGoodsPriceId())
			{
				exits = true;
				preGoods.setNumber(preGoods.getNumber()+goods.getNumber());
				((TextView)row.getChildAt(2)).setText(""+preGoods.getNumber());
			}
		}
		if(!exits)
			showConsumeGoods(goods);
	}
	
	protected void showConsumeGoods(ConsumeModel goods){

//		ConsumeModel goods = handler.fillVacancy(barcode, count, comment);
		
		TextView name = new TextView(ConsumeInsert.this,null,R.style.TextViewfillWrapSmallStyle);		
		TextView unit = new TextView(ConsumeInsert.this,null,R.style.TextViewfillWrapSmallStyle);
		TextView number = new TextView(ConsumeInsert.this,null,R.style.TextViewfillWrapSmallStyle);
		TextView price = new TextView(ConsumeInsert.this,null,R.style.TextViewfillWrapSmallStyle);
		TextView total = new TextView(ConsumeInsert.this,null,R.style.TextViewfillWrapSmallStyle);
		
		name.setText(goods.getGoodsName());
		unit.setText(goods.getUnitName());
		number.setText(""+goods.getNumber());
		price.setText(""+goods.getInPrice());
		total.setText(new String().valueOf(goods.getInPrice()*goods.getNumber()));
		
		final TableLayout table = (TableLayout)findViewById(R.id.consumeInsertTable);
		final TableRow row = new TableRow(ConsumeInsert.this);
		mapping.put(row, goods);
		row.setOnCreateContextMenuListener(new OnCreateContextMenuListener(){

			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
				// TODO Auto-generated method stub
				menuInfo = new AdapterContextMenuInfo(row, 0, 0);
				menu.setHeaderTitle("菜单项");
				menu.add(0, MENU_REMOVE, 0, "删除该记录");
				menu.findItem(MENU_REMOVE).setOnMenuItemClickListener(new OnMenuItemClickListener(){

					public boolean onMenuItemClick(MenuItem item) {
						// TODO Auto-generated method stub
						switch(item.getItemId()){
						case MENU_REMOVE:				
							consumeGoods.remove((ConsumeModel)mapping.get(row));
							mapping.remove(row);	
							table.removeView(row);
							return true;
						}
						return false;
					}
					
				});
			}
			
		});
		
		row.addView(name, 0);
		row.addView(unit, 1);
		row.addView(number, 2);
		row.addView(price, 3);
		row.addView(total, 4);
		table.addView(row);
		
		consumeGoods.add(goods);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		handler = null;
		consumeGoods.clear();
	}

	public class ScanReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String barcode = intent.getStringExtra("BARCODE");
//			new AlertDialog.Builder(ConsumeInsert.this).setMessage("barcode:"+barcode).show();		
			ConsumeModel goods = handler.fillVacancy(barcode, 1, "");		
			addConsumeGoods(goods);
			state = UNSAVE_STATE;
		}
		
	}
}
