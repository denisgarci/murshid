import {DictionaryKey} from "./DictionaryKey";

export class NotInflectedEntry{
  hindi: string;
  urdu: string;
  part_of_speech: string;
  part_of_speech_label: string;
  canonical_keys: string[];
  master_dictionary_key: DictionaryKey;
}
