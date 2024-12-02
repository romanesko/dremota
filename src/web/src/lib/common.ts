import type {User} from "@/models";


export function log(...args: any[]) {
  if(!import.meta.env.VITE_DEBUG) return;
  console.log(...args)
}

// if (!import.meta.env.VITE_BASE_URL) {
//   throw new Error('VITE_BASE_URL is not defined')
// }

export const BASE_URL = import.meta.env.VITE_BASE_URL || "";

export function alertError(e: any) {
  log('alertError', e)
  if (e instanceof Error) {
    e = e.message

  }
  _alert(e,'rgba(255,0,0,0.2)')
}
export function alertSuccess(msg:string) {
  _alert(msg,'rgba(117,250,41,0.4)')
}


export function getUserPhoto(user: User){
  if(!user.photoUrl){
    return 'https://upload.wikimedia.org/wikipedia/commons/thumb/8/83/Telegram_2019_Logo.svg/2048px-Telegram_2019_Logo.svg.png'
  }
  return `${BASE_URL}/images/${user.photoUrl}`
}

export function formatDate(dateString: string) {
  if (!dateString) {
    return ''
  }
  try {
    const d = new Date(dateString)
    const date = d.toISOString().split('T')[0];
    const time = d.toTimeString().split(' ')[0];
    let [year, month, day] = date.split("-")
    let [hour, minute, second] = time.split(":")
    return `${day}.${month}.${year} ${hour}:${minute}`
  } catch (e) {
    return dateString
  }
}

function _alert(str: string, color: string) {
  const container = document.getElementById('toast-container')
  if (!container) {
    return
  }
  let htmlString = `<div class="toast mb-1" style="display:block; opacity: 0;transition: opacity 0.5s ease; "

    role="alert" aria-live="assertive" aria-atomic="true" data-bs-autohide="false" data-bs-toggle="toast">
  <div class="toast-body" style="background:${color}">
    ${str}
  </div>
</div>`

  var div = document.createElement('div');
  div.innerHTML = htmlString.trim();

  container.appendChild(div);

  const fistChild = div.firstChild! as HTMLElement

  // fistChild.classList.add('show')
  setTimeout(() => {
    fistChild.style.opacity = '1'
  }, 100)

  setTimeout(() => {
    fistChild.style.opacity = '0'
    setTimeout(() => {
      container.removeChild(div)
    }, 500)
  }, 3000)

}

