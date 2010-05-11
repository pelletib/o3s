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
	import mx.charts.chartClasses.DataDescription;
   import flash.utils.ByteArray;

	[Bindable]
	[RemoteClass(alias="net.o3s.web.vo.EventVO")] //mapping vers la classe java
	public class MyEvent
	{
		private var _name:String;
		private var _id:int;
		private var _date:Date;
		private var _theDefault:Boolean;
      private var _imageFile:ByteArray;

		public function MyEvent()
		{
			//TODO: implement function
		}

		public function set id(value:int):void
		{
			_id = value;
		}

		public function get id():int
		{
			return _id;
		}

		public function set date(value:Date):void
		{
			_date = value;
		}

		public function get date():Date
		{
			return _date;
		}

		public function get name():String
		{
			return _name;
		}

		public function set name(value:String):void
		{
			_name = value;
		}

		public function get theDefault():Boolean
		{
			return _theDefault;
		}

		public function set theDefault(value:Boolean):void
		{
			_theDefault = value;
		}

      public function get imageFile():ByteArray
      {
         return _imageFile;
      }

      public function set imageFile(value:ByteArray):void
      {
         _imageFile = value;
      }
	}
}