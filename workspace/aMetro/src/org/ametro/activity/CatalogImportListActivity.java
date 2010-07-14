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

import org.ametro.R;
import org.ametro.adapter.CheckedCatalogAdapter;
import org.ametro.catalog.CatalogMap;
import org.ametro.catalog.CatalogMapPair;
import org.ametro.catalog.CatalogMapState;
import org.ametro.catalog.storage.CatalogStorage;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

public class CatalogImportListActivity extends BaseCatalogExpandableActivity {

	protected boolean isCatalogProgressEnabled(int catalogId) {
		return catalogId == CatalogStorage.IMPORT; 
	}

	protected int getEmptyListMessage() {
		return R.string.msg_no_maps_in_import;
	}

	public int getCatalogState(CatalogMap local, CatalogMap remote) {
		return mStorageState.getImportCatalogState(local, remote);
	}

	protected int getDiffMode() {
		return CatalogMapPair.DIFF_MODE_REMOTE;
	}

	protected int getLocalCatalogId() {
		return CatalogStorage.LOCAL;
	}

	protected int getRemoteCatalogId() {
		return CatalogStorage.IMPORT;
	}

	protected int getDiffColors() {
		return R.array.import_catalog_map_state_colors;
	}

	
	/****************** MAIN MENU ********************/
	
	private final int MAIN_MENU_IMPORT = 1;
	private final static int REQUEST_IMPORT = 1;

	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, MAIN_MENU_IMPORT, 4, R.string.menu_import).setIcon(android.R.drawable.ic_menu_add);
		return true;
	}

	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.findItem(MAIN_MENU_IMPORT).setEnabled(mMode == MODE_LIST);
		return super.onPrepareOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MAIN_MENU_IMPORT:
			Intent i = new Intent(this, CatalogMapSelectionActivity.class);
			i.putExtra(CatalogMapSelectionActivity.EXTRA_TITLE, getText(R.string.menu_import));
			i.putExtra(CatalogMapSelectionActivity.EXTRA_REMOTE_ID, CatalogStorage.IMPORT);
			i.putExtra(CatalogMapSelectionActivity.EXTRA_FILTER, mActionBarEditText.getText().toString());
			i.putExtra(CatalogMapSelectionActivity.EXTRA_SORT_MODE, CheckedCatalogAdapter.SORT_MODE_COUNTRY);
			i.putExtra(CatalogMapSelectionActivity.EXTRA_CHECKABLE_STATES, new int[]{ CatalogMapState.IMPORT, CatalogMapState.UPDATE } );
			startActivityForResult(i, REQUEST_IMPORT);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_IMPORT:
			if(resultCode == RESULT_OK && data!=null){
				String[] names = data.getStringArrayExtra(CatalogMapSelectionActivity.EXTRA_SELECTION);
				for (String systemName : names) {
					mStorage.requestImport(systemName);
				}
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
}