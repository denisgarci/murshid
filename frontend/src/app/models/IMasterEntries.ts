import {MasterEntry} from "./MasterEntry";

/**
 * Interface for a map of the master_entries relevant for a song, with the key in the form "टूटे_ 4"
 */
export interface IMasterEntries {
  [key: string]: MasterEntry;
};
