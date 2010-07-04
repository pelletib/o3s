package net.o3s.webflex.components
{
	import mx.controls.Alert;
	import mx.validators.StringValidator;
	import mx.validators.ValidationResult;

	public class AdvancedStringValidator extends StringValidator
	{
		//public var required : Boolean = true;


		public function AdvancedStringValidator()
		{
			super();
			required = true;
		}


		/**
		 * This method overrides the doValidation method of the validator class
		 */
		override protected function doValidation(value:Object) : Array
		{
			super.maxLength = iMaxCharacters;
			super.minLength = iMinCharacters;

			var results : Array = super.doValidation(value);

			// Return if there are errors
			// or if the required property is set to false and length is 0.
			var val:String = value ? String(value) : "";
			if (results.length > 0 || ((val.length == 0) && !required))
				return results;
			else
			    return AdvancedStringValidator.validatePassword(this, value, null);
		}

		/**
		 * Variable
		 * Name 		: iMinCharacters
		 * Data Type 	: int
		 * Purpose		: Specifies the minimum number of characters that the string can contain
		 * Default		: 6
		 *
		 **/
		private var iMinCharacters : int = 2;

		public function get minCharacters() : int
		{
			return iMinCharacters;
		}

		public function set minCharacters(value : int) : void
		{
			iMinCharacters = value;
		}

		private var iMaxCharacters : int = 30;

		public function get maxCharacters() : int
		{
			return iMaxCharacters;
		}

		public function set maxCharacters(value : int) : void
		{
			iMaxCharacters = value;
		}

		private var isSpecialCharacters : Boolean = false;

		public function get allowSpecialCharacters() : Boolean
		{
			return isSpecialCharacters;
		}

		public function set allowSpecialCharacters(value : Boolean) : void
		{
			isSpecialCharacters = value;
		}

		private var isNumbers : Boolean = false;

		public function get allowNumbers() : Boolean
		{
			return isNumbers;
		}

		public function set allowNumbers(value : Boolean) : void
		{
			isNumbers = value;
		}

		private var isSpaces : Boolean = false;

		public function get allowSpaces() : Boolean
		{
			return isSpaces;
		}

		public function set allowSpaces(value : Boolean) : void
		{
			isSpaces = value;
		}


		/***iMinCharacters***/

		private static function validatePassword(validator : AdvancedStringValidator,
											value : Object,
											baseField : String) : Array
		{
			var valResult : Array = new Array();
			var strString : String = String(value);

			if(!validator.allowNumbers)
			{
				var isNum : Boolean = false;
				for(var i : int = 0; i < strString.length; i++)
				{
					if(strString.charCodeAt(i) >= 48 && strString.charCodeAt(i) <= 57)
					{
						isNum = true;
						break;
					}
				}

				if(isNum)
				{
					valResult.push(new ValidationResult(true,baseField,"numeralNotAllowedError",
												validator.numeralNotAllowedError));
				}
				//return valResult;
			}

			if(!validator.allowSpaces)
			{
				var isSpace : Boolean = false;
				for(var j : int = 0; j < strString.length; j++)
				{
					if(strString.charAt(j) == " ")
					{
						isSpace = true;
						break;
					}
				}

				if(isSpace)
				{
					valResult.push(new ValidationResult(true,baseField,"spaceNotAllowedError",
												validator.spaceNotAllowedError));
				}
				//return valResult;
			}

			if(!validator.allowSpecialCharacters)
			{
				var isSpecial : Boolean = false;
				for(var k : int = 0; k < strString.length; k++)
				{
					if(!((strString.charCodeAt(k) == 45) || (strString.charCodeAt(k) == 32) ||
						(strString.charCodeAt(k) >= 65 && strString.charCodeAt(k) <= 90) ||
						(strString.charCodeAt(k) >= 97 && strString.charCodeAt(k) <= 122)))
						{
							isSpecial = true;
							break;
						}
				}

				if(isSpecial)
				{
					valResult.push(new ValidationResult(true,baseField,"specialCharacterNotAllowedError",
												validator.specialCharacterNotAllowedError));
				}

			}
			return valResult;
		}

		/**
		 * Variable
		 * Name 		: _incorrectLengthError
		 * Data Type	: String
		 * Purpose		: Contains the message to be displayed if the entered length of the String is incorrect
		 * Default		: Length of the password string is incorrect
		 **/
		private var _incorrectLengthError : String = "Length of the password string is incorrect";

		public function get incorrectLength() : String
		{
			return _incorrectLengthError;
		}

		public function set incorrectLength(value : String) : void
		{
			if(value != null)
			{
				_incorrectLengthError = value;
			}
		}
		/***_incorrectLength***/

		/**
		 * Variable
		 * Name 		: _specialCharacterMissingError
		 * Data Type	: String
		 * Purpose		: Contains the message to be displayed if there's not even 1 special character in the string
		 * Default		: The Password should contain atleast one special character
		 **/
		private var _specialCharacterNotAllowedError : String = "The string cannot contain special characters";

		public function get specialCharacterNotAllowedError() : String
		{
			return _specialCharacterNotAllowedError;
		}

		public function set specialCharacterNotAllowedError(value : String) : void
		{
			if(value != null)
			{
				_specialCharacterNotAllowedError = value;
			}

		}
		/***_specialCharacterMissingError***/

		/**
		 * Variable
		 * Name 		: _numeralMissingError
		 * Data Type	: String
		 * Purpose		: Contains the message to be displayed if there's not even 1 numeral in the string
		 * Default		: The password should contain atleast one numeral
		 **/
		private var _numeralNotAllowedError : String = "The string cannot contain a numeral";

		public function get numeralNotAllowedError() : String
		{
			return _numeralNotAllowedError;
		}

		public function set numeralNotAllowedError(value : String) : void
		{
			if(value != null)
			{
				_numeralNotAllowedError = value;
			}
		}
		/***_numeralMissingError***/


		/**
		 * Variable
		 * Name 		: _numeralMissingError
		 * Data Type	: String
		 * Purpose		: Contains the message to be displayed if there's not even 1 numeral in the string
		 * Default		: The password should contain atleast one numeral
		 **/
		private var _spaceNotAllowedError : String = "The string cannot contain spaces";

		public function get spaceNotAllowedError() : String
		{
			return _spaceNotAllowedError;
		}

		public function set spaceNotAllowedError(value : String) : void
		{
			if(value != null)
			{
				_spaceNotAllowedError = value;
			}
		}


	}
}
/******************End of Class Definition****************/
