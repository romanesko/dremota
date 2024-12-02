<div class="page">
  <div class="page-wrapper">
    <div class="page-body">
      <div class="container-xl" style="text-align: center;">
        <script
            async
            src="https://telegram.org/js/telegram-widget.js?22"
            data-telegram-login="DremotaTestBot"
            data-size="large"
            data-onauth="onTelegramAuth(user)"
            data-request-access="write"></script>
        <script type="text/javascript">
          function onTelegramAuth(user) {
            processData(user);
          }
        </script>

      </div>
    </div>

  </div>
</div>

<script lang="ts">

  import {alertError, BASE_URL} from "@/lib/common";
  import {setToken} from "@/store/auth";

  function processData(data: any) {

    fetch(`${BASE_URL}/tgauth`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(data)
    })
        .then(res => {
          if (res.status === 200) {
            return res.json()
          } else {
            throw new Error('bad status')
          }
        })
        .then(data => {

          if (data.token) {
            setToken(data.token)
            setTimeout(()=>
              document.location.reload(), 100)
          }
        }).catch(e => {
          alert("Вероятно у вас нет доступа")
      })
  }

  window.processData = processData;

</script>