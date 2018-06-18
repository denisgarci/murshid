import {DictionaryKey} from "./DictionaryKey";

export class InflectedEntry{
  inflected_hindi: string;
  canonical_hindi: string;
  master_dictionary_key: DictionaryKey;
  part_of_speech: string;
  part_of_speech_label: string;
  accidence: string[];
}
