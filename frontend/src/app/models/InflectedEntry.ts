import {DictionaryKey} from "./DictionaryKey";

export class InflectedEntry{
  inflected_hindi: string;
  inflected_urdu: string;
  canonical_hindi: string;
  master_dictionary_key: DictionaryKey;
  canonical_urdu: string;
  part_of_speech: string;
  part_of_speech_label: string;
  accidence: string[];
  canonical_keys: string[];
}
