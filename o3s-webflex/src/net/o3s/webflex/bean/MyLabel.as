/**
 * O3S: Open Source Sport Software
 * Copyright (C) 2010 Benoit Pelletier
 * Contact: btpelletier@gmail.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 *
 * --------------------------------------------------------------------------
 * $Id: pelletib $
 * --------------------------------------------------------------------------
 */
package net.o3s.webflex.bean
{
	[Bindable]
	[RemoteClass(alias="net.o3s.web.vo.LabelVO")] //mapping vers la classe java
	public class MyLabel
	{
		private var _value:String;


		public function MyLabel()
		{
			//TODO: implement function
		}

		public function get value():String
		{
			return _value;
		}

		public function set value(value:String):void
		{
			_value = value;
		}

	}
}