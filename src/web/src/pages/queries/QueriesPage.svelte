<div class="col-12">
  <div class="card">
    <div class="card-header flex-row justify-content-between">
      <h3 class="card-title">Все запросы</h3>
    </div>
    <QueriesTable/>
  </div>

</div>

<script lang="ts">
    import api from "@/lib/api";
    import {alertError} from "@/lib/common";
    import type {Query} from "@/models";
    import QueriesTable from "./components/QueriesTable.svelte";

    let queries = $state([] as Query[]);

  api.getQueries().catch(alertError).then(a => {
    if (a) {
      queries = a
    }
  })
</script>