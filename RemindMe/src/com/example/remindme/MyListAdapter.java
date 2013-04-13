//package com.example.remindme;
//
//import java.util.List;
//
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.Button;
//import android.widget.TextView;
//
//public class MyListAdapter extends BaseAdapter{
//    View renderer;
//    List<Item> items;
//
//            // call this one and pass it layoutInflater.inflate(R.layout.my_list_item)
//    public MyListAdapter(View renderer) {
//        this.renderer = renderer;
//    }
//
//            // whenever you need to set the list of items just use this method.
//            // call it when you have the data ready and want to display it
//    public void setModel(List<Item> items){
//        this.items = items;
//        notifyDataSetChanged();
//    }
//
//    public int getCount() {
//        return items!=null?items.size():0;
//    }
//    public Object getItem(int position) {
//        return items!=null?items.get(position):null;
//    }
//    
//    public long getItemId(int position) {
//        return items!=null?items.get(position).getId():-1;
//    }
//
//    public View getView(int position, View convertView, ViewGroup parent) {
////        if(convertView==null){
////            convertView = renderer;
////        }
////        Item item = items.get(position);
////             // replace those R.ids by the ones inside your custom list_item layout.
////        TextView label = (TextView)convertView.findViewById(R.id.reminder_label);
////        label.setText(item.getMessage());
////        Button button = (Button)convertView.findViewById(R.id.reminder_edit_button);
////        button.setOnClickListener(item.getListener());
//        return convertView;
//    }
//}