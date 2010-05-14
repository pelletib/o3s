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
	[RemoteClass(alias="net.o3s.web.vo.RegisteredVO")] //mapping vers la classe java
	public class Registered
	{
		private var _id:int;

		private var _name:String;

      private var _teamed:Boolean;

      private var _paid:Boolean;

	  private var _arrivalDate:Date;

	  private var _registeringDate:Date;

	  private var _elapsedTime:uint;

      private var _persons:ArrayCollection;

      private var _competition:Competition;

	  private var _event:MyEvent;

	  private var _label:MyLabel;

      private var _category:Category;

		public function Registered()
		{
			//TODO: implement function
		}

		public function get id():int
		{
			return _id;
		}

		public function set id(value:int):void
		{
			_id = value;
		}

		public function get registeringDate():Date {
			return this._registeringDate;
		}

		public function set registeringDate(value:Date):void {
			this._registeringDate = value;
		}

		public function get arrivalDate():Date {
			return this._arrivalDate;
		}

		public function set arrivalDate(value:Date):void {
			this._arrivalDate = value;
		}

		public function get elapsedTime():uint
      {
         return _elapsedTime;
      }

      public function set elapsedTime(value:uint):void
      {
         _elapsedTime = value;
      }

      public function get persons():ArrayCollection
      {
         return _persons;
      }

      public function set persons(value:ArrayCollection):void
      {
         _persons = value;
      }

      public function get label():MyLabel
      {
         return _label;
      }

      public function set label(value:MyLabel):void
      {
         _label = value;
      }

      public function get competition():Competition
      {
         return _competition;
      }

      public function set competition(value:Competition):void
      {
         _competition = value;
      }

      public function get category():Category
      {
         return _category;
      }

      public function set category(value:Category):void
      {
         _category = value;
      }


	  public function get event():MyEvent
	  {
		  return _event;
	  }

	  public function set event(value:MyEvent):void
	  {
		  _event = value;
	  }

	  public function get name():String
		{
			return _name;
		}

		public function set name(value:String):void
		{
			_name = value;
		}

      public function get teamed():Boolean
      {
         return _teamed;
      }

      public function set teamed(value:Boolean):void
      {
         _teamed = value;
      }

      public function get paid():Boolean
      {
         return _paid;
      }

      public function set paid(value:Boolean):void
      {
         _paid = value;
      }

	}
}