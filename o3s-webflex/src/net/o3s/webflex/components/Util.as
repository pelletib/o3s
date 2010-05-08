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
package net.o3s.webflex.components
{
	import mx.rpc.Fault;
	import mx.rpc.events.FaultEvent;
	import mx.controls.Alert;
	import net.o3s.webflex.bean.FlexException;
	import flash.utils.getDefinitionByName;
	import mx.utils.ObjectUtil;
	import mx.rpc.Fault;
	import mx.rpc.events.FaultEvent;
	import flash.events.Event;
	import net.o3s.webflex.bean.Competition;


	public class Util
	{
		public function Util()
		{
		}

		public static function handleException(event:FaultEvent): void {
			var msg : String = event.fault.faultString;
			var clazz : Class = getExceptionClass(event.fault);
			var instance : Error = null;
			if (clazz != null) {
				Alert.show(msg,"Error");
			}  else {
				Alert.show("Erreur ! Si l'erreur persiste, contacter l'administrateur " + event.fault.faultString);
			}
		}

		public static function getExceptionClass(myFault:Fault) : Class {
			var clazz:Class = null;
			var index:int = myFault.faultString.indexOf("FlexException");
			if (index != -1) {
				var cname:String = myFault.faultString.substr(0,index-1);
				try {
					clazz = getDefinitionByName("bean.FlexException") as Class;
				}
				catch (e:ReferenceError) {
				}
			}
			return clazz;
		}

		public static function onFault(ev:Event):void {
			Alert.show( ObjectUtil.toString(ev) );
		}


		// remove unknow and blank entries
		public static function competitionFilter(item:Object):Boolean {
			var c:Competition = item as Competition;
			return (c.name != "Unknown");
		}

	}
}