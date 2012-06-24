/**
 *  This file is part of Saldo Oi.
 *
 *  Saldo Oi is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Saldo Oi is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Saldo Oi. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.thiagovinicius.android.saldooi.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.thiagovinicius.android.saldooi.R;

public class PreferenceComIcone extends Preference {

	Drawable mIcone;

	public PreferenceComIcone(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray atributos = context.obtainStyledAttributes(attrs,
				R.styleable.views_PreferenceComIcone);
		mIcone = atributos
				.getDrawable(R.styleable.views_PreferenceComIcone_icone);
	}

	@Override
	public View getView(View convertView, ViewGroup parent) {
		View view = super.getView(convertView, parent);
		view.setBackgroundDrawable(mIcone);
		return view;
	}

}