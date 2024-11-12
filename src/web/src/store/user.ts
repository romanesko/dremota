import {type Writable, writable} from "svelte/store";
import type {User} from "@/models";

export const currentUser : Writable<User|null> = writable(null)