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
	import mx.collections.ArrayCollection;

	[Bindable]
	[RemoteClass(alias="net.o3s.web.vo.CategoryVO")] //mapping vers la classe java
	public class Category
	{
		private var _id:int;

		private var _name:String;

      private var _shortName:String;

      private var _sex:String;

	  private var _event:MyEvent;

	  private var _competitions:ArrayCollection;

	  private var _minDate:Date;

	  private var _maxDate:Date;

		public function Category()
		{
			//TODO: implement function
		}

		public function get competitions():ArrayCollection
		{
			return _competitions;
		}

		public function set competitions(value:ArrayCollection):void
		{
			_competitions = value;
		}

		public function get event():MyEvent {
			return this._event;
		}

		public function set event(value:MyEvent):void {
			this._event = value;
		}


		public function get minDate():Date {
			return this._minDate;
		}

		public function set minDate(value:Date):void {
			this._minDate = value;
		}

		public function get maxDate():Date {
			return this._maxDate;
		}

		public function set maxDate(value:Date):void {
			this._maxDate = value;
		}

		public function get id():int
		{
			return _id;
		}

		public function set id(value:int):void
		{
			_id = value;
		}

		public function get name():String
		{
			return _name;
		}

		public function set name(value:String):void
		{
			_name = value;
		}
      public function get shortName():String
      {
         return _shortName;
      }

      public function set shortName(value:String):void
      {
         _shortName = value;
      }

   	  public function get sex():String
      {
         return _sex;
      }

      public function set sex(value:String):void
      {
         _sex = value;
      }

	}
}