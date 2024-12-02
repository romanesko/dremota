import {writable} from 'svelte/store';
import {log} from "@/lib/common";
import api from "@/lib/api";

export const auth_token = writable('');

export function setToken(token: string) {
  if(!token) {
    eraseCookie('token');
    auth_token.set('');
    return;
  }
  setCookie('token', token, 1);
  auth_token.set(token);
}

function setCookie(name: string, value: string, days: number) {
  let expires = "";
  if (days) {
    const date = new Date();
    date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
    expires = "; expires=" + date.toUTCString();
  }
  log('writing cookie',"=" + (value || "")  + expires + "; path=/")
  document.cookie = name + "=" + (value || "")  + expires + "; path=/";
}

function eraseCookie(name: string) {
  document.cookie = name + '=; Path=/; Expires=Thu, 01 Jan 1970 00:00:01 GMT;';
  auth_token.set('');
}


function getCookieValue(cookieName: string) {
  // Split document.cookie into individual cookies
  const cookies = document.cookie.split("; ");
  log("Getting cookie", cookieName)

  // Loop through cookies to find the one with the specified name
  for (const cookie of cookies) {
    const [name, value] = cookie.split("=");
    if (name === cookieName) {
      log("Found cookie",cookieName,"with value", value)
      return decodeURIComponent(value);
    }
  }

  log("Cookie", cookieName, "not found")

  // Return null if the cookie is not found
  return null;
}



const token = getCookieValue('token');
if (token) {
  api.setToken(token);
  auth_token.set(token);
}