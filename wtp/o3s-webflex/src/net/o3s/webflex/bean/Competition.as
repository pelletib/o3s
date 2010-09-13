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
	[RemoteClass(alias="net.o3s.web.vo.CompetitionVO")] //mapping vers la classe java
	public class Competition
	{
		private var _name:String;
		private var _id:int;
		private var _event:MyEvent;
		private var _startingDate:Date;
		private var _lowerLabelNumber:int;
		private var _higherLabelNumber:int;
		private var _lastLabelNumber:int;
		private var _teamed:Boolean;


		public function Competition()
		{
			//TODO: implement function
		}

		public function get event():MyEvent {
			return this._event;
		}

		public function set event(value:MyEvent):void {
			this._event = value;
		}

		public function get startingDate():Date {
			return this._startingDate;
		}

		public function set startingDate(value:Date):void {
			this._startingDate = value;
		}

		public function get lowerLabelNumber():int
		{
			return _lowerLabelNumber;
		}

		public function set lowerLabelNumber(value:int):void
		{
			_lowerLabelNumber = value;
		}

		public function get higherLabelNumber():int
		{
			return _higherLabelNumber;
		}

		public function set higherLabelNumber(value:int):void
		{
			_higherLabelNumber = value;
		}

		public function get lastLabelNumber():int
		{
			return _lastLabelNumber;
		}

		public function set lastLabelNumber(value:int):void
		{
			_lastLabelNumber = value;
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

		public function get teamed():Boolean
		{
			return _teamed;
		}

		public function set teamed(value:Boolean):void
		{
			_teamed = value;
		}

	}
}