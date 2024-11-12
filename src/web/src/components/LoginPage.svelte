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

  function processData(data: any) {

    fetch(`${BASE_URL}/tgauth`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(data)
    })
        .then(res => res.json())
        .then(data => {
          console.log('data', data)
          if (data.token) {
            document.cookie = 'token=' + data.token;
            setTimeout(document.location.reload, 1000);
          }
        }).catch(e => {
          alertError(e)
      })
  }

  window.processData = processData;

</script>