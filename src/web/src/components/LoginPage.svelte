<div class="page">
  <div class="page-wrapper">
    <div class="page-body">
      <div class="container-xl " style="height:100lvh; text-align: center;align-content: center;">
        <div class="row">
          <div class="col"></div>


          <div class="col-3">
            {#if data.code}
              отправьте боту команду:
              <pre class="mt-4">/login {data.code}</pre>

            {:else}
              вы не авторизованы

              <div class="mt-4">
                <button class="btn" onclick={login}>ВОЙТИ</button>
              </div>

            {/if}

          </div>
          <div class="col"></div>
        </div>


      </div>
    </div>

  </div>
</div>

<script lang="ts">

  import {alertError, alertSuccess, BASE_URL, log} from "@/lib/common";
  import {setToken} from "@/store/auth";
  import api from "@/lib/api";

  let data = $state({code: ''})

  function login() {
    api.getLoginCode().then(a => {
      data = a
      checkLoggedIn()
    })

  }


  function checkLoggedIn() {
    if (!data.code) {
      return
    }
    api.checkLoggedIn(data).then(a => {
      log('checkToken', data)
      if (a.token) {
        doLogin(a.token)
        return
      }
      setTimeout(() => {
        checkLoggedIn()
      }, 1000)
    }).catch(alertError)
  }

  function doLogin(token: string) {
    setToken(token)
    setTimeout(() => {
      document.location.href = '/#/'
      document.location.reload()
    }, 100)

  }

</script>
