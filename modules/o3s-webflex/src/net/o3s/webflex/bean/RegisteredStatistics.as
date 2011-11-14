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
	import mx.collections.ArrayCollection;

	[Bindable]
	[RemoteClass(alias="net.o3s.web.vo.RegisteredStatisticsVO")] //mapping vers la classe java
	public class RegisteredStatistics
	{
      private var _competition:String;
      private var _category:String;
      private var _registeredNumber:int;
      private var _arrivalNumber:int;

		public function RegisteredStatistics()
		{
			//TODO: implement function
		}

		public function get competition():String
		{
			return _competition;
		}

		public function set competition(value:String):void
		{
			_competition = value;
		}

		public function get category():String {
			return this._category;
		}

		public function set category(value:String):void {
			this._category = value;
		}

		public function get registeredNumber():int {
			return this._registeredNumber;
		}

		public function set registeredNumber(value:int):void {
			this._registeredNumber = value;
		}

		public function get arrivalNumber():int
      {
         return _arrivalNumber;
      }

      public function set arrivalNumber(value:int):void
      {
         _arrivalNumber = value;
      }

	}
}