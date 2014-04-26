package com.appointext.frontend;

import com.bmsce.appointext.R;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

public class TNCDisplay extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tnc);
		TextView dis = (TextView)findViewById(R.id.displayTC);
		dis.setText("AppoinText - the automatic Reminder Generator\n" +
				"Authors: Abhishek Kasargod, Aparajita Raychaudhury, Ashwin S, Manasa G\n\n" +
				"The source code for this project can be found at https://github.com/CodeGood/AppoinText " +
				"This program is free software. You can resdistribute it and/or modify it under the terms of the " +
				"GNU General Public License, as published by the Free Software Foundation " +
				", either version 3 or (at your option) any later version. " +
				"The program is distributed in the hopes that it will be useful but " +
				"WITHOUT ANY WARRANTY, without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR USE " +
				"See the GPU General Public License for more details." );
		dis.setShadowLayer(5, 0, 0, Color.BLACK);

	}
}
