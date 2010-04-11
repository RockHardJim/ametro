/*
 * http://code.google.com/p/ametro/
 * Transport map viewer for Android platform
 * Copyright (C) 2009-2010 Roman.Golovanov@gmail.com and other
 * respective project committers (see project home page)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package org.ametro.activity;

import java.util.ArrayList;

import org.ametro.R;
import org.ametro.adapter.RouteListViewAdapter;
import org.ametro.adapter.StationListAdapter;
import org.ametro.model.MapView;
import org.ametro.model.StationView;
import org.ametro.model.route.RouteView;
import org.ametro.util.DateUtil;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class BrowseRoute extends Activity implements OnClickListener,
		OnItemClickListener {

	private ListView mRouteList;
	private ImageButton mFavoritesButton;
	private ListView mStationList;
	private TextView mTextTime;

	private MapView mMapView;
	private RouteView mRoute;

	private int mFromId;
	private int mToId;
	
	private boolean isChecked;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.browse_route);
		
		mRouteList = (ListView) findViewById(R.id.browse_route_list_view);
		mStationList = (ListView) findViewById(R.id.browse_route_stations);
		mTextTime = (TextView) findViewById(R.id.browse_route_time_text);
		mFavoritesButton = (ImageButton) findViewById(R.id.browse_route_favorites);

		mMapView = BrowseVectorMap.Instance.getMapView();
		mRoute = BrowseVectorMap.Instance.getNavigationRoute();
		RouteListViewAdapter adapter = new RouteListViewAdapter(this, mRoute,
				mMapView);
		adapter.setTextColor(Color.WHITE);
		mRouteList.setAdapter(adapter);
		mRouteList.setOnItemClickListener(this);

		ArrayList<StationView> stations = new ArrayList<StationView>();
		stations.add(mRoute.getStationFrom());
		stations.add(mRoute.getStationTo());
		StationListAdapter stationListAdapter = new StationListAdapter(this,
				stations, mMapView);
		stationListAdapter.setTextColor(Color.WHITE);
		mStationList.setAdapter(stationListAdapter);
		mStationList.setEnabled(false);

		mFromId = mRoute.getStationFrom().id;
		mToId = mRoute.getStationTo().id;
		
		isChecked = BrowseVectorMap.Instance.isFavoriteRoute(mFromId, mToId);
		updateFavoritesButton();
		mFavoritesButton.setOnClickListener(this);

		mTextTime.setText(getString(R.string.msg_route_time) + " "
				+ DateUtil.getTimeHHMM(mRoute.getTime()));
		
		mStationList.setDividerHeight(0);
		//mRouteList.setDividerHeight(0);
	}

	private void updateFavoritesButton() {
		if (isChecked) {
			mFavoritesButton
					.setImageResource(android.R.drawable.btn_star_big_on);
		} else {
			mFavoritesButton
					.setImageResource(android.R.drawable.btn_star_big_off);
		}
	}

	public void onClick(View v) {
		if (v == mFavoritesButton) {
			isChecked = !isChecked;
			if(isChecked){
				BrowseVectorMap.Instance.addFavoriteRoute(mFromId,mToId);
			}else{
				BrowseVectorMap.Instance.removeFavoriteRoute(mFromId,mToId);
			}
			updateFavoritesButton();

			Toast.makeText(this, 
					isChecked 
					? getString(R.string.msg_route_added_to_favorites)
					: getString(R.string.msg_route_removed_from_favorites),
							Toast.LENGTH_SHORT).show();
		}
	}

	public void onItemClick(AdapterView<?> av, View v, int position, long id) {
		StationView station = mMapView.stations[(int) id];
		BrowseVectorMap.Instance.setCurrentStation(station);
		finish();
	}
}