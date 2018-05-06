import {DictionaryEntry} from "./DictionaryEntry";

export class DictionariesContent {
  accidence: string[];
  accidence_labels: string[];
  canonical_hindi: string;
  inflected_hindi: string;
  inflected_part_of_speech_label: string;
  dictionary_entries: DictionaryEntry[];
}
