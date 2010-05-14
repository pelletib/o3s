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
	[RemoteClass(alias="net.o3s.web.vo.PersonVO")] //mapping vers la classe java
	public class Person
	{
		private var _id:int;

		private var _firstname:String;

		private var _lastname:String;

		private var _birthday:Date;

		private var _sex:String;

		private var _club:String;

		private var _license:String;

		private var _email:String;

		private var _computedCategory:String;

		public function Person()
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

		public function get lastname():String
		{
			return _lastname;
		}

		public function set lastname(value:String):void
		{
			_lastname = value;
		}

		public function get firstname():String
		{
			return _firstname;
		}

		public function set firstname(value:String):void
		{
			_firstname = value;
		}

		public function get birthday():Date
		{
			return _birthday;
		}

		public function set birthday(value:Date):void
		{
			_birthday = value;
		}

		public function get sex():String
		{
			return _sex;
		}

		public function set sex(value:String):void
		{
			_sex = value;
		}

		public function get email():String
		{
			return _email;
		}

		public function set email(value:String):void
		{
			_email = value;
		}

		public function get club():String
		{
			return _club;
		}

		public function set club(value:String):void
		{
			_club = value;
		}

		public function get license():String
		{
			return _license;
		}

		public function set license(value:String):void
		{
			_license = value;
		}

		public function get computedCategory():String
		{
			return _computedCategory;
		}

		public function set computedCategory(value:String):void
		{
			_computedCategory = value;
		}

	}
}