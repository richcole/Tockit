extern "C" {

#include <sarl/map.h>
}

#include <sarl/test.h>

int main(int num_args, char **args)
{
  Sarl_Map* m = sarl_map_create();

  sarl_map_insert_pair(m, sarl_pair(4, 10));
  sarl_map_insert_pair(m, sarl_pair(5, -10));
  
  SARL_TEST_ASSERT_EQ(sarl_map_image(m, 4), 10);
  SARL_TEST_ASSERT_EQ(sarl_map_image(m, 5), -10);

  sarl_map_insert(m, 4,15);
  SARL_TEST_ASSERT_EQ(sarl_map_image(m, 4),15);

  sarl_map_decr_ref(m);
};
