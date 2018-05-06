import {InflectedEntry} from "./InflectedEntry";

/**
 * Interface for a map of the inflected_entries relevant for a song, with the key in the form "WIKITIONARY_टूटे_4"
 */
export interface IInflectedEntries {
  [key: string]: InflectedEntry;
};
